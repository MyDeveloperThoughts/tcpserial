package org.mdt.tcpmodem.hayes;

public class TerminalTypeOptionHandler extends SimpleHandler
{
    private String m_type;

    public TerminalTypeOptionHandler(String type)
    {
        m_type = type;
    }

    public NVTOption newSubOption(byte code, byte[] data)
    {
        return new TerminalTypeSubOption(data);
    }

    public void optionDataReceived(OptionEvent event)
    {
        TerminalTypeSubOption tto = (TerminalTypeSubOption) event.getOption();
        if (tto.valueRequired())
        {
            tto.setTerminalType(m_type);
            sendSubOption(event.getOutputStream(), event.getOption());
        }
    }
}
