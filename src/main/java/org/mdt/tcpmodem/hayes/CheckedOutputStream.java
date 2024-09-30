package org.mdt.tcpmodem.hayes;

import java.io.IOException;
import java.io.OutputStream;

public class CheckedOutputStream extends OutputStream
{
    private OutputStream _os;

    public CheckedOutputStream()
    {
        super();
    }

    public synchronized void write(int data) throws IOException
    {
        if (_os != null)
        {
            _os.write(data);
        }
    }

    public synchronized void write(byte[] data) throws IOException
    {
        if (_os != null)
        {
            _os.write(data);
        }
    }

    public synchronized void write(byte[] data, int start, int len) throws IOException
    {
        if (_os != null)
        {
            _os.write(data, start, len);
        }
    }

    public synchronized void setOutputStream(OutputStream os)
    {
        _os = os;
    }

}
