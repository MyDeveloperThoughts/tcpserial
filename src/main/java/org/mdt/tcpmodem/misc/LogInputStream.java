package org.mdt.tcpmodem.misc;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

public class LogInputStream extends FilterInputStream
{
    private Logger m_log;

    public LogInputStream(InputStream is, String prefix) {
        super(is);
        m_log = Logger.getLogger(prefix);
    }

    public int read() throws IOException
    {
        int rc=in.read();
        if(rc != -1) {
            byte b[]=new byte[1];
            b[0]=(byte)rc;
            m_log.fine(Utility.dumpHex(b));
        }
        return rc;
    }

    public int read(byte[] data, int start, int len) throws IOException {
        int rc=in.read(data,start,len);
        if(rc > -1) {
            m_log.fine(Utility.dumpHex(data,start,rc));
        }
        return rc;
    }
}
