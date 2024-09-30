package org.mdt.tcpmodem.config;

import java.util.ArrayList;
import java.util.List;

public final class Config
{
    private static final List<ModemPool> m_modemPools = new ArrayList<>();

    public static List<ModemPool> getModemPools()
    {
        return m_modemPools;
    }
}
