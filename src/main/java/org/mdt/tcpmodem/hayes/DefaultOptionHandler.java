package org.mdt.tcpmodem.hayes;

public class DefaultOptionHandler extends AbstractOptionHandler
{
    public NVTOption newSubOption(byte code, byte[] data)
    {
        return new NVTOption(code);
    }

    public void doReceived(OptionEvent event)
    {
        sendWONTOption(event);
    }

    public void willReceived(OptionEvent event)
    {
        sendDONTOption(event);
    }

    public void optionDataReceived(OptionEvent event)
    {
    }

}
