package org.mdt.tcpmodem.hayes;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IP232Port extends AbstractIPDCEPort
{
    private static final Logger m_log = Logger.getLogger(IP232Port.class.getName());
    private String m_modemName = "";

    public IP232Port(String modemName, int port, int speed) throws PortException
    {
        super(port, speed);
        m_modemName = modemName;
    }

    public void run()
    {
        Socket socket = null;
        InputStream is;
        PipedOutputStream pos;
        byte[] data = new byte[1024];
        int len;

        try
        {
            pos = new PipedOutputStream(m_inputStream);
            while (true)
            {
                try
                {
                    socket = m_listenSocket.accept();
                    m_log.log(Level.INFO, "[" + m_modemName + "] Port: " + + socket.getPort() + " Connection opened");
                    _os.setOutputStream(socket.getOutputStream());
                    is = socket.getInputStream();
                    setDTR(true);
                    while ((len = is.read(data)) > -1)
                    {
                        pos.write(data, 0, len);
                        sendEvent(new DCEEvent(this, DCEEvent.DATA_AVAILABLE, false, true));
                    }
                }
                catch (IOException e)
                {
                    Level logLevel = e.getMessage()!=null && e.getMessage().equals("Connection reset") ? Level.INFO : Level.SEVERE;
                    m_log.log(logLevel, "[" + m_modemName + "] Port: " + (socket==null ? "n/a" : socket.getPort()) + " " + e.getMessage());
                }
                finally
                {
                    if (socket != null)
                    {
                        m_log.log(Level.INFO, "[" + m_modemName + "] Port: " + socket.getPort() + " Connection closed.");
                        socket.close();
                    }
                    socket = null;
                    _os.setOutputStream(null);
                    // bring DTR low
                    setDTR(false);

                }
            }
        }
        catch (IOException e)
        {
            m_log.log(Level.SEVERE, "PipedInputStream creation failed", e);
        }

    }
}
