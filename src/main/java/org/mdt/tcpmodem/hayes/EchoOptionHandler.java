package org.mdt.tcpmodem.hayes;

public class EchoOptionHandler extends AbstractOptionHandler
{
    private final boolean m_init;
    private final boolean m_localEcho;
    private final boolean m_remoteEcho;

    public EchoOptionHandler(boolean bLocalEcho, boolean bRemoteEcho, boolean bInit)
    {
        m_init = bInit;
        m_localEcho = bLocalEcho;
        m_remoteEcho = bRemoteEcho;
    }

    public void doReceived(OptionEvent event)
    {
        if (m_localEcho)
            sendWILLOption(event);
        else
            sendWONTOption(event);
    }

    public void willReceived(OptionEvent event)
    {
        if (m_remoteEcho)
            sendDOOption(event);
        else
            sendDONTOption(event);
    }

    public void optionDataReceived(OptionEvent event)
    {
    }
}
