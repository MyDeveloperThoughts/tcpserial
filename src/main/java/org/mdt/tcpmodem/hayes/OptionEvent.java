package org.mdt.tcpmodem.hayes;

import java.util.EventObject;

public class OptionEvent extends EventObject
{
    private NVTOption m_option;
    private NVTOutputStream m_outputStream;

    public OptionEvent(NVTInputStream o, NVTOption option, NVTOutputStream os)
    {
        super(o);
        m_outputStream = os;
        m_option = option;
    }

    public NVTOutputStream getOutputStream()
    {
        return m_outputStream;
    }

    public NVTOption getOption()
    {
        return m_option;
    }

}
