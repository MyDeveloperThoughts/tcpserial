package org.mdt.tcpmodem.hayes;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NVTInputStream extends FilterInputStream
{
    private static final int STATE_DATA = 0;
    private static final int STATE_IAC = 1;
    private static final int STATE_DO = 2;
    private static final int STATE_DONT = 3;
    private static final int STATE_WILL = 4;
    private static final int STATE_WONT = 5;
    private static final int STATE_SB = 6;
    private static final int STATE_SB_DATA = 7;
    private static final int STATE_SB_IAC = 8;
    private static final Logger m_log = Logger.getLogger(NVTInputStream.class.getName());

    private byte[] m_buffer;
    private int m_position;
    private int m_state = STATE_DATA;
    private int m_optionEnd;
    private int m_optionStart;
    private byte m_subOption;
    private NVTOutputStream m_outputStream;

    private final List<OptionEventHandler> m_optionEventHandlers = new ArrayList<>();
    private final Map<Byte, OptionEventHandler> m_optionToOpenEventHandlersMap = new HashMap<>();

    private OptionEventHandler m_defaultHandler = new DefaultOptionHandler()
    {
        public void doReceived(OptionEvent event)
        {
            if (event.getOption().getOptionCode() == NVTOption.OPT_SUPPRESS_GO_AHEAD)
                sendWILLOption(event);
            else
                sendWONTOption(event);
        }

        public void willReceived(OptionEvent event)
        {
            if (event.getOption().getOptionCode() == NVTOption.OPT_SUPPRESS_GO_AHEAD)
                sendDOOption(event);
            else
                sendDONTOption(event);
        }
    };

    public NVTInputStream(InputStream is, NVTOutputStream os)
    {
        super(is);
        m_outputStream = os;
        m_buffer = new byte[1024];
        m_position = 0;
    }

    public NVTInputStream(InputStream is, NVTOutputStream os, boolean bCachedIAC)
    {
        this(is, os);
        m_buffer[0] = NVTOption.IAC;
        m_position++;
    }

    public synchronized void addOptionEventListener(OptionEventHandler listener)
    {
        m_optionEventHandlers.add(listener);
    }

    public synchronized void removeOptionEventListener(OptionEventHandler listener)
    {
        m_optionEventHandlers.remove(listener);
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#read()
     */
    public int read() throws IOException
    {
        byte b[] = new byte[1];
        int rc = read(b, 0, 1);
        if (rc < 0)
            return rc;
        return b[0];
    }

    private int parse() throws IOException
    {
        int plen = m_optionEnd;
        int pos = m_optionEnd;
        NVTOption option;
        OptionEventHandler handler;

        while (plen < m_position)
        {
            switch (m_state)
            {
                case STATE_DATA:
                    // we are parsing data.
                    if (m_buffer[plen] == NVTOption.IAC)
                    {
                        m_state = STATE_IAC;
                    }
                    else if (plen != pos)
                    {
                        m_buffer[pos++] = m_buffer[plen];
                    }
                    else
                    {
                        pos++;
                    }
                    plen++;
                    break;
                case STATE_IAC:
                    // we found an IAC
                    switch (m_buffer[plen++])
                    {
                        case NVTOption.IAC:
                            // unescape an IAC
                            m_buffer[pos++] = NVTOption.IAC;
                            m_state = STATE_DATA;
                            break;
                        case NVTOption.WILL:
                            m_state = STATE_WILL;
                            break;
                        case NVTOption.WONT:
                            m_state = STATE_WONT;
                            break;
                        case NVTOption.DO:
                            m_state = STATE_DO;
                            break;
                        case NVTOption.DONT:
                            m_state = STATE_DONT;
                            break;
                        case NVTOption.SB:
                            m_state = STATE_SB;
                            // stack option data right after last regular byte
                            m_optionStart = pos;
                            m_optionEnd = pos;
                            break;
                        default:
                            // it's a normal NVT Command.
                            break;
                    }
                    break;
                case STATE_WILL:
                    handler = getHandler(m_buffer[plen]);
                    option = new NVTOption(m_buffer[plen++]);
                    m_log.log(Level.FINE, "Received WILL " + option);
                    handler.willReceived(new OptionEvent(this, option, m_outputStream));
                    m_state = STATE_DATA;
                    break;
                case STATE_WONT:
                    handler = getHandler(m_buffer[plen]);
                    option = new NVTOption(m_buffer[plen++]);
                    m_log.log(Level.FINE, "Received WONT " + option);
                    handler.wontReceived(new OptionEvent(this, option, m_outputStream));
                    m_state = STATE_DATA;
                    break;
                case STATE_DO:
                    handler = getHandler(m_buffer[plen]);
                    option = new NVTOption(m_buffer[plen++]);
                    m_log.log(Level.FINE, "Received DO " + option);
                    handler.doReceived(new OptionEvent(this, option, m_outputStream));
                    m_state = STATE_DATA;
                    break;
                case STATE_DONT:
                    handler = getHandler(m_buffer[plen]);
                    option = new NVTOption(m_buffer[plen++]);
                    m_log.log(Level.FINE, "Received DONT " + option);
                    handler.dontReceived(new OptionEvent(this, option, m_outputStream));
                    m_state = STATE_DATA;
                    break;
                case STATE_SB:
                    m_subOption = m_buffer[plen++];
                    m_log.log(Level.FINE, "Received SB " + m_subOption);
                    m_state = STATE_SB_DATA;
                    break;
                case STATE_SB_DATA:
                    switch (m_buffer[plen])
                    {
                        case NVTOption.IAC:
                            m_state = STATE_SB_IAC;
                            break;
                        default:
                            m_buffer[m_optionEnd++] = m_buffer[plen];
                            break;
                    }
                    plen++;
                    break;
                case STATE_SB_IAC:
                    switch (m_buffer[plen])
                    {
                        case NVTOption.IAC:
                            //it's an escaped IAC.  Uescape.
                            m_buffer[m_optionEnd++] = m_buffer[plen++];
                            break;
                        case NVTOption.SE:
                            m_log.log(Level.FINE, "Received SE");
                            //it's the end of the suboption;
                            byte b[] = new byte[m_optionEnd - m_optionStart];
                            System.arraycopy(m_buffer, m_optionStart, b, 0, m_optionEnd - m_optionStart);
                            handler = getHandler(m_subOption);
                            option = handler.newSubOption(m_buffer[plen++], b);
                            m_log.log(Level.FINE, "Received SUBOPTION " + option);
                            handler.optionDataReceived(new OptionEvent(this, option, m_outputStream));
                            m_state = STATE_DATA;
                            plen++;
                            m_state = STATE_DATA;
                            m_optionStart = m_optionEnd = 0;
                            break;
                        default:
                            // what do we do here?
                    }
                    break;
            }
        } // we ran out of bytes to parse.
        if (m_optionEnd != 0)
            m_position = m_optionEnd;
        else
            m_position = pos;
        return pos;
    }

    /*
     *  (non-Javadoc)
     * @see java.io.InputStream#read(byte[], int, int)
     *
     * The idea here is to do a read of data from the
     */

    public int read(byte[] data, int start, int len) throws IOException
    {
        int l;
        int plen;
        // read some data into our internal buffer.
        l = in.read(m_buffer, m_position, m_buffer.length - m_position);
        if (l < 1)
        {
            // no bytes, or -1;
            return l;
        }
        // we read more data.
        m_position += l;
        // parse the data
        plen = parse();
        if (plen == 0)
        {
            // all data was NVT commands, grab some more.
            return read(data, start, len);
        }
        else
        {
            // plen is how many chars in buffer now.
            // get minimum
            l = Math.min(Math.min(len, data.length - start), plen);
            // copy to outbuf.
            System.arraycopy(m_buffer, 0, data, start, l);
            // move buffer up.
            // Move unparsed data up to end of parsed data
            //arraycopy(src,srcPos,dest,destPos,len);
            System.arraycopy(m_buffer, l, m_buffer, 0, m_position - l);
            if (m_optionEnd != 0)
            {
                m_optionEnd -= l;
                m_optionStart -= l;
            }
            m_position -= l;
            return l;
        }
    }

    public void registerOptionHandler(byte option, OptionEventHandler handler)
    {
        m_optionToOpenEventHandlersMap.put(option, handler);
    }

    public void unregisterOptionHandler(byte option)
    {
        m_optionToOpenEventHandlersMap.remove(option);
    }

    private OptionEventHandler getHandler(byte option)
    {
        OptionEventHandler handler = m_optionToOpenEventHandlersMap.get(option);
        if (handler == null)
            handler = m_defaultHandler;
        return handler;
    }

}
