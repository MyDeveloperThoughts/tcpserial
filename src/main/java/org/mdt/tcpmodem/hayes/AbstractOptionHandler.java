package org.mdt.tcpmodem.hayes;

import java.io.IOException;

public abstract class AbstractOptionHandler implements OptionEventHandler
{
    public NVTOption newSubOption(byte code, byte[] data)
    {
        return new NVTOption(code);
    }

    public void dontReceived(OptionEvent event)
    {
        sendWONTOption(event);
    }

    public void wontReceived(OptionEvent event)
    {
        sendDONTOption(event);
    }

    protected void sendWILLOption(OptionEvent event)
    {
        try
        {
            event.getOutputStream().sendWILLOption(event.getOption());
        }
        catch (IOException e)
        {
            // dont care
        }
    }

    protected void sendWONTOption(OptionEvent event)
    {
        try
        {
            event.getOutputStream().sendWONTOption(event.getOption());
        }
        catch (IOException e)
        {
            // dont care
        }
    }

    protected void sendDOOption(OptionEvent event)
    {
        try
        {
            event.getOutputStream().sendDOOption(event.getOption());
        }
        catch (IOException e)
        {
            // dont care
        }
    }

    protected void sendDONTOption(OptionEvent event)
    {
        try
        {
            event.getOutputStream().sendDONTOption(event.getOption());
        }
        catch (IOException e)
        {
            // dont care
        }
    }

    protected void sendSubOption(NVTOutputStream os, NVTOption option)
    {
        try
        {
            os.sendSubOption(option);
        }
        catch (IOException e)
        {
            // dont care
        }
    }
}
