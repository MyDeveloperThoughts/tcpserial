package org.mdt.tcpmodem.config;

import org.mdt.tcpmodem.hayes.Line;

import java.util.ArrayList;
import java.util.List;

public final class ModemPool
{
    private final   List<Modem> m_modems = new ArrayList<>();
    private Line    m_line = null;

    public List<Modem> getModems()
    {
        return m_modems;
    }

    public Line getLine()
    {
        return m_line;
    }

    public void setLine(Line line)
    {
        m_line = line;
    }
}
