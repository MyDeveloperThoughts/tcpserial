package org.mdt.tcpmodem.hayes;

import java.io.IOException;
import java.io.PipedOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NVT232Port extends AbstractIPDCEPort
{
    private static final Logger m_log = Logger.getLogger(NVT232Port.class.getName());

    private class Handler extends ComPortOptionHandler
    {
        public Handler(boolean bServer)
        {
            super(bServer);
        }

        public void optionDataReceived(OptionEvent event)
        {
            switch (((ComPortSubOption) event.getOption()).getSubOptionCode())
            {
                case ComPortSubOption.SUB_OPT_SET_CONTROL:
                    ComPortSetControlOption option = (ComPortSetControlOption) event.getOption();
                    switch (option.getCommandCode())
                    {
                        case ComPortSetControlOption.COMMAND_SET_DTR_ON:
                            NVT232Port.this.setDTR(true);
                            sendSubOption(event.getOutputStream(), new ComPortSetControlOption(m_server, ComPortSetControlOption.COMMAND_SET_DTR_ON));
                            break;
                        case ComPortSetControlOption.COMMAND_SET_DTR_OFF:
                            NVT232Port.this.setDTR(false);
                            sendSubOption(event.getOutputStream(), new ComPortSetControlOption(m_server, ComPortSetControlOption.COMMAND_SET_DTR_OFF));
                            break;
                    }
                    break;
            }
        }
    }

    public NVT232Port(int port, int speed) throws PortException
    {
        super(port, speed);
    }

    public NVT232Port(String host, int port, int speed) throws PortException
    {
        super(host, port, speed);
    }

    public void run()
    {
        Socket socket = null;
        NVTInputStream inputStream;
        PipedOutputStream pos;
        byte[] data = new byte[1024];
        int len;
        boolean isServer = m_listenSocket != null;

        try
        {
            pos = new PipedOutputStream(m_inputStream);
            while (true)
            {
                try
                {
                    if (isServer)
                        socket = m_listenSocket.accept();
                    else
                        socket = new Socket(m_host, m_port);
                    NVTOutputStream nos = new NVTOutputStream(socket.getOutputStream());
                    _os.setOutputStream(nos);
                    inputStream = new NVTInputStream(socket.getInputStream(), nos);
                    // add handlers
                    inputStream.registerOptionHandler(ComPortSubOption.OPT_COM_PORT, new Handler(isServer));
                    setDTR(true);
                    while ((len = inputStream.read(data)) > -1)
                    {
                        pos.write(data, 0, len);
                        sendEvent(new DCEEvent(this, DCEEvent.DATA_AVAILABLE, false, true));
                    }
                }
                catch (IOException e)
                {
                    m_log.log(Level.SEVERE, "Error during socket read", e);
                }
                finally
                {
                    if (socket != null)
                        socket.close();
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
