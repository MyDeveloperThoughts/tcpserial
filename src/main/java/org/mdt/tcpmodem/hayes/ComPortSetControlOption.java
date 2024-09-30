package org.mdt.tcpmodem.hayes;


public class ComPortSetControlOption extends ComPortSubOption
{
    public static final byte COMMAND_SET_DTR_ON = 8;
    public static final byte COMMAND_SET_DTR_OFF = 9;
    private byte m_command;

    public ComPortSetControlOption(boolean bServer, byte cmd)
    {
        super(bServer, ComPortSubOption.SUB_OPT_SET_CONTROL);
        // TODO assume data.length=2?
        m_command = cmd;
    }

    public byte getCommandCode()
    {
        return m_command;
    }

}
