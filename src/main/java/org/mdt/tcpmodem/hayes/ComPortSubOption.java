package org.mdt.tcpmodem.hayes;

public class ComPortSubOption extends NVTOption
{
    public static final byte OPT_COM_PORT = 44;
    public static final byte SUB_OPT_SET_CONTROL = 5;

    private byte m_subOption;
    private boolean m_server;

    protected ComPortSubOption(boolean bServer, byte data)
    {
        super(OPT_COM_PORT);
        m_subOption = data;
        m_server = bServer;
    }

    public byte getSubOptionCode()
    {
        return m_subOption;
    }

}
