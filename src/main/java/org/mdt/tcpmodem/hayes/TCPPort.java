package org.mdt.tcpmodem.hayes;

import org.mdt.tcpmodem.exceptions.LineNotAnsweringException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TCPPort extends Thread implements LinePort
{
    private boolean _bDCD = false;
    private boolean _bDSR = false;
    private static final Logger m_log = Logger.getLogger(TCPPort.class.getName());
    private Socket m_socket;
    private final List<LineEventListener> m_lineEventListeners = new ArrayList<>();
    private PipedInputStream m_pipedInputStream;
    private PipedOutputStream m_pipedOutputStream;
    private OutputStream m_outputStream;
    private boolean m_isRunning = false;
    private Timer m_ringer;

    private class RingTask extends TimerTask
    {
        public void run()
        {
            TCPPort.this.sendEvent(new LineEvent(TCPPort.this, LineEvent.RI, false, true));
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
            }
            TCPPort.this.sendEvent(new LineEvent(TCPPort.this, LineEvent.RI, true, false));
        }
    }

    public TCPPort(String address) throws PortException
    {
        int pos = address.indexOf(":");
        String host;
        int port = 23;

        if (pos > -1)
        {
            // trim off spaces if name : port
            host = address.substring(0, pos).trim();
            try
            {
                port = Integer.parseInt(address.substring(pos + 1).trim());
            }
            catch (NumberFormatException e)
            {
                // port is still 23
                // probably should throw an error
                throw new PortException("'" + address.substring(pos + 1).trim() + "' is not a valid port", e);
            }
        }
        else
        {
            host = address;
        }

        try
        {
            initSocket(new Socket(host, port));
        }
        catch (UnknownHostException e)
        {
            // what should we throw for a bad number?
            m_log.log(Level.SEVERE, e.getMessage(), e);
            throw new LineNotAnsweringException("Host " + host + " unknown", e);
        }
        catch (ConnectException e)
        {
            // what should we throw for a Connection refused?
            m_log.log(Level.SEVERE, e.getMessage(), e);
            throw new LineNotAnsweringException("Connnection to " + host + " refused", e);
        }
        catch (IOException e)
        {
            m_log.log(Level.SEVERE, e.getMessage(), e);
            throw new PortException("IO Error", e);
        }
        setDaemon(true);
        // we are connected.
        setDCD(true);
    }

    private void initSocket(Socket socket) throws IOException
    {
        m_socket = socket;
        m_pipedOutputStream = new PipedOutputStream();
        m_pipedInputStream = new PipedInputStream(m_pipedOutputStream);
        m_outputStream = m_socket.getOutputStream();
    }

    public TCPPort(Socket socket) throws PortException
    {
        RingTask ringtask = new RingTask();
        try
        {
            initSocket(socket);
            m_ringer = new Timer();
            m_ringer.scheduleAtFixedRate(ringtask, 0, 4000);
        }
        catch (IOException e)
        {
            m_log.log(Level.SEVERE, e.getMessage(), e);
            throw new PortException("IO Error", e);
        }
        setDaemon(true);
    }

    public void run()
    {
        InputStream is;
        byte[] data = new byte[1024];
        int len = 0;

        m_isRunning = true;
        try
        {
            is = m_socket.getInputStream();
            len = is.read();
            if ((byte) len == NVTOption.IAC)
            {
                // telnet session
                m_outputStream = new NVTOutputStream(m_outputStream);
                is = new NVTInputStream(is, (NVTOutputStream) m_outputStream, true);
                ((NVTInputStream) is).registerOptionHandler(NVTOption.OPT_ECHO, new EchoOptionHandler(false, true, false));
                ((NVTInputStream) is).registerOptionHandler(NVTOption.OPT_TRANSMIT_BINARY, new TransmitBinaryOptionHandler());
                ((NVTInputStream) is).registerOptionHandler(NVTOption.OPT_TERMINAL_TYPE, new TerminalTypeOptionHandler("ANSI"));
            }
            else if (len > -1)
            {
                m_pipedOutputStream.write(len);
            }
            else
            {
                // bad connection.
                // we'll reset() in the finally clause.
            }
            while (m_isRunning && (len = is.read(data)) > -1)
            {
                m_pipedOutputStream.write(data, 0, len);
                sendEvent(new LineEvent(this, LineEvent.DATA_AVAILABLE, false, true));
                // send event.
            }
        }
        catch (IOException e)
        {
            if (m_isRunning)
                m_log.log(Level.SEVERE, "Error while receiving data", e);
        }
        finally
        {
            reset();
        }
    }

    public boolean isDSR()
    {
        return true;
    }

    public boolean isDCD()
    {
        return m_isRunning;
    }

    public void setDTR(boolean b)
    {
        setDSR(b);
        if (!b)
            reset();
    }

    private void reset()
    {
        if (m_ringer != null)
        {
            // turn off ringer.
            m_ringer.cancel();
        }
        // turn off
        m_isRunning = false;
        setDCD(false);
        try
        {
            m_socket.close();
        }
        catch (IOException e)
        {
        }
    }

    private void setDSR(boolean b)
    {
        if (_bDSR != b)
        {
            // send DSR change event
            sendEvent(new LineEvent(this, LineEvent.DSR, _bDSR, b));
            _bDSR = b;
        }
    }


    private void setDCD(boolean b)
    {
        if (_bDCD != b)
        {
            // send DCD change event
            sendEvent(new LineEvent(this, LineEvent.CD, _bDCD, b));
            _bDCD = b;
        }
    }

    public void addEventListener(LineEventListener lsnr)
    {
        m_lineEventListeners.add(lsnr);
    }

    public void removeEventListener(LineEventListener listener)
    {
        if (m_lineEventListeners.contains(listener))
            m_lineEventListeners.remove(listener);
    }

    private void sendEvent(LineEvent event)
    {
        if (m_lineEventListeners.size() > 0)
            for (int j = 0; j < m_lineEventListeners.size(); j++)
                m_lineEventListeners.get(j).lineEvent(event);
    }

    public void setFlowControl(int control)
    {
        // do nothing.
    }

    public InputStream getInputStream() throws IOException
    {
        return m_pipedInputStream;
    }

    public OutputStream getOutputStream() throws IOException
    {
        return m_outputStream;
    }

    public int getSpeed()
    {
        return BPS_UNKNOWN;
    }

    public boolean isDTR()
    {
        return m_isRunning;
    }

    public boolean isRI()
    {
        return false;
    }

    public void answer() throws IOException
    {
        m_ringer.cancel();
        setDCD(true);
    }

}
