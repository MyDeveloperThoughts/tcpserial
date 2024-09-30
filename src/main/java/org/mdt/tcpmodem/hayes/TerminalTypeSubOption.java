package org.mdt.tcpmodem.hayes;

public class TerminalTypeSubOption extends NVTOption
{
    private static final byte SUB_OPT_TERMINAL_TYPE_IS = 0;
    private static final byte SUB_OPT_TERMINAL_TYPE_SEND = 1;
    boolean m_isRequired = true;
    String m_type;

    public TerminalTypeSubOption(byte[] data)
    {
        super(OPT_TERMINAL_TYPE);
        if (data != null && data.length > 0)
        {
            m_isRequired = (data[0] == SUB_OPT_TERMINAL_TYPE_SEND);
            if (data.length > 1)
                m_type = new String(data, 1, data.length - 1);
        }
    }

    public boolean valueRequired()
    {
        return m_isRequired;
    }

    public String getTerminalType()
    {
        return m_type;
    }

    public void setTerminalType(String type)
    {
        m_type = type;
        m_isRequired = false;
    }

    public byte[] getOptionData()
    {
        byte[] data;
        if (valueRequired())
        {
            data = new byte[1];
            data[0] = SUB_OPT_TERMINAL_TYPE_SEND;
        }
        else
        {
            data = new byte[1 + m_type.length()];
            data[0] = SUB_OPT_TERMINAL_TYPE_IS;
            byte[] t = m_type.getBytes();
            System.arraycopy(t, 0, data, 1, t.length);
        }
        return data;
    }

    public String toString()
    {
        if (valueRequired())
            return "VALUE REQUIRED for OPTION " + getOptionCode();
        else
            return "VALUE '" + getTerminalType() + "' supplied for OPTION " + getOptionCode();
    }

}

