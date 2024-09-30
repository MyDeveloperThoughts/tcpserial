package org.mdt.tcpmodem.hayes;

public class SimpleHandler extends AbstractOptionHandler
{
    public void doReceived(OptionEvent event)
    {
        sendWILLOption(event);
    }

    public void willReceived(OptionEvent event)
    {
        sendDOOption(event);
    }

    public void optionDataReceived(OptionEvent event)
    {
    }
}
