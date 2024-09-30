package org.mdt.tcpmodem.hayes;

public abstract class ComPortOptionHandler extends AbstractOptionHandler
{
    protected boolean m_server;

    public ComPortOptionHandler(boolean bServer)
    {
        m_server = bServer;
    }

    public void doReceived(OptionEvent event)
    {
        sendWILLOption(event);
    }

    public void willReceived(OptionEvent event)
    {
        sendDOOption(event);
    }

    public NVTOption newSubOption(byte code, byte[] data)
    {
        boolean bServer = data[0] >= 100;
        switch (data[0] % 100)
        {
            case ComPortSubOption.SUB_OPT_SET_CONTROL:
                return new ComPortSetControlOption(bServer, data[1]);
            default:
                return new NVTOption(code);
        }
    }
}
