package org.mdt.tcpmodem.hayes;

import org.mdt.tcpmodem.hayes.commands.CommandResponse;
import org.mdt.tcpmodem.hayes.commands.DialCommand;
import org.mdt.tcpmodem.exceptions.CommandException;
import org.mdt.tcpmodem.exceptions.LineNotAnsweringException;
import org.mdt.tcpmodem.misc.LogInputStream;
import org.mdt.tcpmodem.misc.LogOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ModemCore
{
    private LinePortFactory m_linePortFactory;
    private int m_rings = 0;
    private int m_connDir;
    private static final int CONNDIR_NONE = 0;
    private static final int CONNDIR_OUTGOING = 1;
    private static final int CONNDIR_INCOMING = 2;
    private EscapeTimer m_timer;
    private LogInputStream m_isLine;
    private LogOutputStream m_osLine;
    private boolean m_DCDInverted = false;
    private DialCommand m_lastNumber;
    private static final Logger m_log = Logger.getLogger(ModemCore.class.getName());
    private boolean m_DSR;
    private boolean m_DCD;
    private boolean m_DTR;

    private final DCEPort m_dcePort;
    private LinePort m_linePort;

    private boolean m_foundA;
    private boolean m_inCmd;
    private int m_dceSpeed;
    private int m_dteSpeed;

    private boolean m_cmdMode;
    private boolean m_offHook;
    private CommandTokenizer m_lastAction;
    private byte[] m_lineBytes = new byte[1024];
    private byte[] m_dceByteData = new byte[1024];
    private byte[] m_lineByteData = new byte[1024];
    private int m_line_len = 0;
    private ModemConfig m_modemConfig;

    private boolean m_output = true;

    private InputStream m_isDCE;
    private OutputStream m_osDCE;

    private final List<ModemEventListener> m_listeners = new ArrayList<>();

    private final LineEventListener m_lineEventListener = new LineEventListener()
    {
        public void lineEvent(LineEvent event)
        {
            ModemCore.this.handleLineEvent(event);
        }
    };

    private DCEEventListener m_dceEventListener = new DCEEventListener()
    {
        public void dceEvent(DCEEvent event)
        {
            ModemCore.this.handleDCEEvent(event);
        }
    };

    public ModemCore(DCEPort port, ModemConfig modemConfig, LinePortFactory linePortFactory)
    {
        m_dcePort = port;
        m_linePortFactory = linePortFactory;
        try
        {
            m_isDCE = new LogInputStream(port.getInputStream(), "Serial In");
            m_osDCE = new LogOutputStream(port.getOutputStream(), "Serial Out");
            this.setConfig(modemConfig);
            m_timer = new EscapeTimer(this);
            getDCEPort().removeEventListener(m_dceEventListener);
            getDCEPort().addEventListener(m_dceEventListener);
            port.start();
            reset();
        }
        catch (IOException e)
        {
            /// hmm what to do
            m_log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    protected void handleDCEEvent(DCEEvent event)
    {
        CommandResponse response;

        switch (event.getEventType())
        {
            case DCEEvent.DATA_AVAILABLE:
                // read some data and parse.
                try
                {
                    int len = m_isDCE.read(m_dceByteData);
                    parseData(m_dceByteData, len);
                }
                catch (IOException e)
                {
                    m_log.log(Level.SEVERE, e.getMessage(), e);
                    /// hmm what to do
                }
                break;
            case DCEEvent.DTR:
                if (!event.getNewValue())
                {
                    // line terminated
                    switch (m_modemConfig.getDTRAction())
                    {
                        case 1:
                            if (!isCommandMode())
                            {
                                setCommandMode(true);
                                sendResponse(ResponseMessage.OK, "DTR triggered switch to command mode");
                            }
                            break;
                        case 2:
                            // hangup
                            response = hangup();
                            if (response.getResponse() != ResponseMessage.OK)
                            {
                                sendResponse(response.getResponse(), "DTR triggered hangup");
                            }
                            break;
                        case 3:
                            reset();
                            sendResponse(ResponseMessage.OK, "DTR triggered reset");
                            break;

                    }
                }
                break;

        }
    }

    protected void handleLineEvent(LineEvent event)
    {
        switch (event.getEventType())
        {
            case LineEvent.DATA_AVAILABLE:
                try
                {
                    // read data
                    int len = m_isLine.read(m_lineByteData);
                    if (!isCommandMode())
                    {
                        // write to dce if in data mode.
                        m_osDCE.write(m_lineByteData, 0, len);
                    }
                }
                catch (IOException e)
                {
                    m_log.log(Level.SEVERE, e.getMessage(), e);
                    // hmm what to do?
                }
                break;
            case LineEvent.RI:
                if (event.getNewValue())
                {
                    // RRRIIIINNNGGG!
                    fireEvent(new ModemEvent(this, ModemEvent.RING));
                    sendResponse(ResponseMessage.RING, "");
                    m_rings++;
                    if (m_modemConfig.getRegister(0) != 0 && m_modemConfig.getRegister(0) == m_rings)
                    {
                        // answer the phone
                        try
                        {
                            answer();
                        }
                        catch (PortException e)
                        {
                            // what to do here?
                            m_log.log(Level.SEVERE, e.getMessage(), e);
                        }
                    }
                }
                break;
            case LineEvent.CD:
                if (!event.getNewValue())
                {
                    // line terminated
                    sendResponse(hangup().getResponse(), "Carrier Lost");
                }
                break;

        }
    }

    public void parseData(byte[] data, int len) throws IOException
    {
        byte ch;
        // handle commands...
        if (m_cmdMode)
        {
            // echo char
            if (m_modemConfig.isEcho() && m_output)
                m_osDCE.write(data, 0, len);
            // command mode
            for (int i = 0; i < len; i++)
            {
                ch = data[i];
                if (m_inCmd)
                {
                    if (ch == m_modemConfig.getRegister(3))
                    {
                        m_log.log(Level.FINE, "Parsing AT Command: " + new String(m_lineBytes, 0, m_line_len));
                        // exec cmd
                        m_lastAction = new CommandTokenizer(m_lineBytes, m_line_len);
                        execCmdLine(m_lastAction);
                        m_line_len = 0;
                        m_inCmd = false;
                        m_foundA = false;
                    }
                    else if (ch == m_modemConfig.getRegister(5) && m_line_len == 0)
                    {
                        if (m_modemConfig.isEcho())
                            m_osDCE.write('T');
                    }
                    else if (ch == m_modemConfig.getRegister(5))
                    {
                        m_line_len--;
                    }
                    else if (m_line_len < 1024)
                    {
                        m_lineBytes[m_line_len++] = (byte) ch;
                    }
                    else
                    {
                        // too big.
                    }
                }
                else if (m_foundA)
                {
                    if (Character.toLowerCase((char) ch) == 't')
                    {
                        m_inCmd = true;
                    }
                    else if (ch == '/')
                    {
                        if (m_lastAction != null)
                        {
                            // only if an action has happened
                            m_lastAction.reset();
                            execCmdLine(m_lastAction);
                        }
                        m_line_len = 0;
                        m_inCmd = false;
                        m_foundA = false;
                    }
                    else if (Character.toLowerCase((char) ch) == 'a')
                    {
                        // do nothing.
                    }
                    else
                    {
                        m_foundA = false;
                    }
                }
                else if (!m_foundA && Character.toLowerCase((char) ch) == 'a')
                {
                    m_foundA = true;
                }
            }
        }
        else if (getConnDirection() != CONNDIR_NONE)
        {
            m_timer.checkData(data, 0, len);
            // data to send to remote side.
            try
            {
                m_osLine.write(data, 0, len);
            }
            catch (IOException e)
            {
                m_log.log(Level.SEVERE, e.getMessage(), e);
                hangup();
            }
        }
        else
        {
            // we went to data mode, but no conn, so go back on hook and in cmd mode
            setCommandMode(true);
            setOffHook(false);
            if (getConnDirection() == CONNDIR_INCOMING)
            {
                sendResponse(ResponseMessage.NO_CARRIER, "no incoming connection");
            }
            else
            {
                sendResponse(ResponseMessage.OK, "no outgoing connection");
            }
        }
    }

    public int getConnDirection()
    {
        return m_connDir;
    }

    public CommandResponse hangup()
    {
        fireEvent(new ModemEvent(this, ModemEvent.HANGUP));
        m_dcePort.setDCD(false);
        setConnDirection(CONNDIR_NONE);
        setCommandMode(true);
        setOffHook(false);
        if (getLinePort() != null)
        {
            // line disconnected
            // close it down, and unlisten
            killLinePort();
            return new CommandResponse(ResponseMessage.NO_CARRIER, "");
        }
        else
        {
            return new CommandResponse(ResponseMessage.OK, "");
        }
    }

    private void killLinePort()
    {
        LinePort port = getLinePort();
        if (port != null)
        {
            port.removeEventListener(m_lineEventListener);
            if (port.isDTR())
                port.setDTR(false);
            try
            {
                setLinePort(null);
            }
            catch (PortException e)
            {
                ;
            }
        }
    }

    public void setConnDirection(int i)
    {
        m_connDir = i;

    }

    protected void execCmdLine(CommandTokenizer cmdline)
    {
        Command cmd;
        CommandResponse resp;
        boolean isDone = false;
        while (!isDone)
        {
            try
            {
                // get next command
                cmd = cmdline.next();
                if (cmd != null)
                {
                    resp = cmd.execute(this);
                    if (resp != CommandResponse.OK)
                    {
                        isDone = true;
                        sendResponse(resp.getResponse(), resp.getText());
                    }
                }
                else if (!isDone)
                {
                    isDone = true;
                    sendResponse(ResponseMessage.OK, "");
                }
            }
            catch (CommandException e)
            {
                isDone = true;
                m_log.log(Level.SEVERE, e.getMessage(), e);
                // print ERROR
                sendResponse(ResponseMessage.ERROR, e.getMessage());
            }
        }
    }

    public boolean isOffHook()
    {
        return m_offHook;
    }

    public boolean isCommandMode()
    {
        return m_cmdMode;
    }

    public boolean isWaitingForCall()
    {
        return m_modemConfig.getRegister(0) != 0 && !isOffHook();
    }

    public CommandResponse answer() throws PortException
    {
        fireEvent(new ModemEvent(this, ModemEvent.PRE_ANSWER));
        if (getLinePort() != null)
        {
            getDCEPort().setDCD(true);
            getLinePort().setDTR(true);
            try
            {
                getLinePort().answer();
            }
            catch (IOException e)
            {
                throw new PortException("Line cannot be answered", e);
            }
            sendResponse(ResponseMessage.getConnectResponse(getSpeed(), m_modemConfig.getResponseLevel()), "");
            setConnDirection(CONNDIR_INCOMING);
        }
        setOffHook(true);
        setCommandMode(false);
        if (getConnDirection() != CONNDIR_NONE)
        {
            fireEvent(new ModemEvent(this, ModemEvent.ANSWER));
            getLinePort().start();
        }
        return CommandResponse.OK;
    }

    public int getSpeed()
    {
        // if line active, get speed from it, else from DCE.
        int speed;
        if (getLinePort() != null && getLinePort().getSpeed() != ModemPort.BPS_UNKNOWN)
        {
            speed = getLinePort().getSpeed();
        }
        else
        {
            speed = getDCEPort().getSpeed();
        }
        return speed;
    }

    public boolean acceptCall(LinePort call) throws PortException
    {
        m_rings = 0;
        boolean rc = false;
        // can;t check for DTR, as it doesn;t work right on COM DCE ports.
        //if(this.getDCEPort().isDTR() && !isOffHook() && getLinePort()== null) {
        if (!isOffHook() && getLinePort() == null)
        {
            rc = true;
            setLinePort(call);
        }
        return rc;
    }

    public CommandResponse dial(DialCommand cmd) throws PortException
    {
        // update last number dialed
        setLastNumber(cmd);
        // go offhook.
        setOffHook(true);
        if (cmd.getData().length() != 0)
        {
            try
            {
                fireEvent(new ModemEvent(this, ModemEvent.DIAL));
                setLinePort(m_linePortFactory.createLinePort(cmd));
                // go to data mode.
                setDCD(true);
                getLinePort().setDTR(true);
                setConnDirection(CONNDIR_OUTGOING);
                fireEvent(new ModemEvent(this, ModemEvent.PRE_CONNECT));
                sendResponse(ResponseMessage.getConnectResponse(getSpeed(), m_modemConfig.getResponseLevel()), "");
                setCommandMode(false);
                fireEvent(new ModemEvent(this, ModemEvent.CONNECT));
                getLinePort().start();
                return CommandResponse.OK;
            }
            catch (LineNotAnsweringException e)
            {
                setOffHook(false);
                fireEvent(new ModemEvent(this, ModemEvent.RESPONSE_NO_ANSWER));
                return new CommandResponse(ResponseMessage.NO_ANSWER, e.getMessage());
            }
            catch (LineBusyException e)
            {
                setOffHook(false);
                fireEvent(new ModemEvent(this, ModemEvent.RESPONSE_BUSY));
                return new CommandResponse(ResponseMessage.BUSY, e.getMessage());
            }
            catch (PortException e)
            {
                setOffHook(false);
                fireEvent(new ModemEvent(this, ModemEvent.RESPONSE_ERROR));
                m_log.log(Level.SEVERE, e.getMessage(), e);
                throw e;
            }
        }
        else
        {
            // atd, just go off hook, and switch to data mode
            setCommandMode(false);
            return CommandResponse.OK;
        }
    }

    public void setCommandMode(boolean b)
    {
        if (m_cmdMode != b)
        {
            if (b)
                fireEvent(new ModemEvent(this, ModemEvent.CMD_MODE));
            else
                fireEvent(new ModemEvent(this, ModemEvent.DATA_MODE));
        }
        m_cmdMode = b;
        synchronized (m_timer)
        {
            m_timer.interrupt();
        }
    }

    public ModemConfig getConfig()
    {
        return m_modemConfig;
    }

    public DialCommand getLastNumber()
    {
        return m_lastNumber;

    }

    public DCEPort getDCEPort()
    {
        return m_dcePort;

    }

    public void setLastNumber(DialCommand command)
    {
        m_lastNumber = command;
    }

    public void setOffHook(boolean b)
    {
        if (b != m_offHook)
        {
            if (b)
                fireEvent(new ModemEvent(this, ModemEvent.OFF_HOOK));
            else
                fireEvent(new ModemEvent(this, ModemEvent.ON_HOOK));
        }
        m_offHook = b;
    }

    protected void setLinePort(LinePort port) throws PortException
    {
        m_linePort = port;
        if (port != null)
        {
            try
            {
                m_osLine = new LogOutputStream(port.getOutputStream(), "Line Out");
                m_isLine = new LogInputStream(port.getInputStream(), "Line In");
                port.addEventListener(m_lineEventListener);
            }
            catch (IOException e)
            {
                m_log.log(Level.SEVERE, e.getMessage(), e);
                throw new PortException("IO Error", e);
            }
        }
        else
        {
            m_osLine = null;
            m_isLine = null;
        }
    }

    public LinePort getLinePort()
    {
        return m_linePort;
    }

    public void sendResponse(ResponseMessage message, String text)
    {
        // emulation of some weird behavior.  All is great if verbose is on, but
        // off changes the rules completely.
        StringBuffer stringBuffer = new StringBuffer();
        if (!m_modemConfig.isQuiet() && isCommandMode())
        {
            if (m_modemConfig.isVerbose())
            {
                stringBuffer.append(message.getText(m_modemConfig.getResponseLevel()));
                if (m_modemConfig.getResponseLevel() == 99 && text != null && !text.isEmpty())
                {
                    // add text
                    stringBuffer.append(" (");
                    stringBuffer.append(text);
                    stringBuffer.append(")");
                }
                stringBuffer.append((char) m_modemConfig.getRegister(3));
                stringBuffer.append((char) m_modemConfig.getRegister(4));
                sendResponse(stringBuffer.toString());
            }
            else
            {
                try
                {
                    m_log.log(Level.FINE, "Sending response code :" + message.getCode());
                    if (m_output)
                    {
                        m_osDCE.write(Integer.toString(message.getCode()).getBytes());
                        m_osDCE.write((char) m_modemConfig.getRegister(3));
                    }
                }
                catch (IOException e)
                {
                    m_log.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        }
    }

    public void sendResponse(String string)
    {
        if (!m_modemConfig.isQuiet() && isCommandMode() && m_output)
        {
            try
            {
                m_log.log(Level.FINE, "Sending response data: " + (string.replaceAll("\r\n", "<cr>")));
                // crlf
                m_osDCE.write((char) m_modemConfig.getRegister(3));
                m_osDCE.write((char) m_modemConfig.getRegister(4));
                m_osDCE.write(string.getBytes());
            }
            catch (IOException e)
            {
                // hmm what to do?
                m_log.log(Level.SEVERE, e.getMessage(), e);
            }
        }
    }

    public void setDCD(boolean b)
    {
        b = m_modemConfig.isDCDForced() ? true : b;
        m_dcePort.setDCD(isDCDInverted() ? !b : b);
    }

    public boolean isDCDInverted()
    {
        return m_DCDInverted;
    }

    public void setConfig(ModemConfig config)
    {
        m_modemConfig = config;

    }

    public void reset()
    {
        // shut down any connections, unlisten to things, etc.
        m_foundA = false;
        m_inCmd = false;
        m_lastAction = null;

        this.setDCD(false);
        this.setCommandMode(true);
        this.setOffHook(false);
    }

    public void setFlowControl(int i)
    {
        getConfig().setFlowControl(i);
        m_dcePort.setFlowControl(i);
        if (getLinePort() != null)
            getLinePort().setFlowControl(i);
    }

    public void setOutput(boolean b)
    {
        m_output = b;
    }

    public void setDCDInverted(boolean b)
    {
        m_DCDInverted = b;
    }

    protected void fireEvent(ModemEvent event)
    {
        if (event != null && m_listeners.size() > 0)
        {
            for (ModemEventListener listener : m_listeners)
                listener.handleEvent(event);
        }
    }


    public void addEventListener(ModemEventListener lsnr)
    {
        m_listeners.add(lsnr);
    }

    public void removeEventListener(ModemEventListener listener)
    {
        if (m_listeners.contains(listener))
            m_listeners.remove(listener);
    }
}
