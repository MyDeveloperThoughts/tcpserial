package org.mdt.tcpmodem.misc;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogOutputStream extends FilterOutputStream
{
    private Logger m_log;

    public LogOutputStream(OutputStream os, String prefix)
    {
        super(os);
        m_log = Logger.getLogger(prefix);
    }

    public void write(int data) throws IOException
    {
        byte b[] = new byte[1];
        b[0] = (byte) data;
        m_log.log(Level.FINE, Utility.dumpHex(b));
        out.write(data);
    }

    public void write(byte[] data, int start, int len) throws IOException
    {
        m_log.log(Level.FINE, Utility.dumpHex(data, start, len));
        out.write(data, start, len);
    }
}
