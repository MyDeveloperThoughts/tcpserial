package org.mdt.tcpmodem.hayes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AbstractIPDCEPort extends Thread implements DCEPort
{
    private static final Logger m_log = Logger.getLogger(AbstractIPDCEPort.class.getName());
    private final int m_speed;
    private boolean m_DSR;
    private boolean m_RI;
    private boolean m_DCD;
    private boolean m_DTR;
    private final List<DCEEventListener> m_listeners = new ArrayList<>();
    protected PipedInputStream m_inputStream = new PipedInputStream();
    protected ServerSocket m_listenSocket;
    protected String m_host;
    protected int m_port;

    protected CheckedOutputStream _os = new CheckedOutputStream();

    public AbstractIPDCEPort(int port, int speed) throws PortException
    {
        m_speed = speed;
        try
        {
            m_listenSocket = new ServerSocket(port);
        } catch (IOException e)
        {
            m_log.log(Level.SEVERE, e.getMessage(), e);
            throw new PortException("Listen Error", e);
        }
        setDaemon(true);
    }

    public AbstractIPDCEPort(String host, int port, int speed)
    {
        m_speed = speed;
        m_host = host;
        m_port = port;
        setDaemon(true);
    }

    public void setDTR(boolean b)
    {
        if (b != m_DTR)
        {
            sendEvent(new DCEEvent(this, DCEEvent.DTR, m_DTR, b));
        }
        m_DTR = b;
    }

    public void setDCD(boolean b)
    {
        m_DCD = b;

    }

    public boolean isDTR()
    {
        return m_DTR;
    }

    public void addEventListener(DCEEventListener lsnr)
    {
        m_listeners.add(lsnr);
    }

    public void removeEventListener(DCEEventListener listener)
    {
        m_listeners.remove(listener);
    }

    public void setFlowControl(int control)
    {
        // do nothing
    }

    public InputStream getInputStream()
    {
        return m_inputStream;
    }

    public OutputStream getOutputStream() throws IOException
    {
        return _os;
    }

    public int getSpeed()
    {
        return m_speed;
    }

    protected void sendEvent(DCEEvent event)
    {
        if (m_listeners.size() > 0)
            for (DCEEventListener listener : m_listeners)
                listener.dceEvent(event);
    }

    public boolean isDCD()
    {
        return m_DCD;
    }

    public void setRI(boolean b)
    {
        m_RI = b;

    }

    public boolean isRI()
    {
        return m_RI;
    }

    public void setDSR(boolean b)
    {
        m_DSR = b;

    }

    public boolean isDSR()
    {
        return m_DSR;
    }

}
