package org.mdt.tcpmodem.hayes;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NVTOutputStream extends FilterOutputStream
{
    private static final Logger m_log = Logger.getLogger(NVTOutputStream.class.getName());

    public NVTOutputStream(OutputStream os)
    {
        super(os);
    }

    /* (non-Javadoc)
     * @see java.io.OutputStream#write(int)
     */
    public synchronized void write(int data) throws IOException
    {
        if ((byte) data == NVTOption.IAC)
        {
            out.write(NVTOption.IAC);
        }
        out.write(data);
    }

    public void write(byte[] data) throws IOException
    {
        write(data, 0, data.length);
    }

    public synchronized void write(byte[] data, int start, int len) throws IOException
    {
        int i = start;
        int end = start + len;

        for (i = start; i < end; i++)
        {
            if (data[i] == (NVTOption.IAC))
            {
                out.write(data, start, i - start + 1);
                out.write(NVTOption.IAC);
                start = i + 1;
            }
        }
        out.write(data, start, end - start);
    }

    public synchronized void sendCommand(byte command) throws IOException
    {
        byte[] b = new byte[2];
        b[0] = NVTOption.IAC;
        b[1] = command;
        out.write(b);
        m_log.log(Level.FINE, "Sent CMD " + command);
    }

    public synchronized void sendWONTOption(NVTOption option) throws IOException
    {
        byte[] b = new byte[3];
        b[0] = NVTOption.IAC;
        b[1] = NVTOption.WONT;
        b[2] = option.getOptionCode();
        out.write(b);
        m_log.log(Level.FINE, "Sent WONT " + option);
    }

    public synchronized void sendDONTOption(NVTOption option) throws IOException
    {
        byte[] b = new byte[3];
        b[0] = NVTOption.IAC;
        b[1] = NVTOption.DONT;
        b[2] = option.getOptionCode();
        out.write(b);
        m_log.log(Level.FINE, "Sent DONT " + option);
    }

    public synchronized void sendWILLOption(NVTOption option) throws IOException
    {
        byte[] b = new byte[3];
        b[0] = NVTOption.IAC;
        b[1] = NVTOption.WILL;
        b[2] = option.getOptionCode();
        out.write(b);
        m_log.log(Level.FINE, "Sent WILL " + option);
    }

    public synchronized void sendDOOption(NVTOption option) throws IOException
    {
        byte[] b = new byte[3];
        b[0] = NVTOption.IAC;
        b[1] = NVTOption.DO;
        b[2] = option.getOptionCode();
        out.write(b);
        m_log.log(Level.FINE, "Sent DO " + option);
    }

    public synchronized void sendSubOption(NVTOption option) throws IOException
    {
        if (option.getOptionData() != null)
        {
            byte[] b = new byte[3];
            b[0] = NVTOption.IAC;
            b[1] = NVTOption.SB;
            b[2] = option.getOptionCode();
            out.write(b);
            write(option.getOptionData());
            b[0] = NVTOption.IAC;
            b[1] = NVTOption.SE;
            out.write(b, 0, 2);
            m_log.log(Level.FINE, "Sent SUBOPTION " + option);
        }
    }

}
