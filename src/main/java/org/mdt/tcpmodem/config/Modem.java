package org.mdt.tcpmodem.config;

import org.mdt.tcpmodem.hayes.EventActionInfo;

import java.util.ArrayList;
import java.util.List;

public final class Modem
{
    private final List<PhoneBookEntry> m_phoneBookEntries = new ArrayList<>();
    private final List<String> m_initializations = new ArrayList<>();
    private final List<EventActionInfo> m_eventActionInfos = new ArrayList<>();
    private Modem m_captiveModem = null;

    private String m_name = "";
    private String m_type = "";
    private String m_device = "";
    private int    m_speed = 38400;
    private String m_parity = null;
    private Integer m_stopbits = null;
    private Boolean m_invertDCD = null;
    private boolean m_playModemSounds;

    public List<PhoneBookEntry> getPhoneBookEntries()
    {
        return m_phoneBookEntries;
    }
    public List<EventActionInfo> getEventActionInfos() { return m_eventActionInfos; }

    public String getName()
    {
        return m_name;
    }

    public void setName(String name)
    {
        m_name = name;
    }

    public String getType()
    {
        return m_type;
    }

    public void setType(String type)
    {
        m_type = type;
    }

    public String getDevice()
    {
        return m_device;
    }

    public void setDevice(String device)
    {
        m_device = device;
    }

    public int getSpeed()
    {
        return m_speed;
    }

    public void setSpeed(int speed)
    {
        m_speed = speed;
    }

    public String getParity()
    {
        return m_parity;
    }

    public void setParity(String parity)
    {
        m_parity = parity;
    }

    public Integer getStopbits()
    {
        return m_stopbits;
    }

    public void setStopbits(Integer stopbits)
    {
        m_stopbits = stopbits;
    }

    public Boolean getInvertDCD()
    {
        return m_invertDCD;
    }

    public void setInvertDCD(Boolean invertDCD)
    {
        m_invertDCD = invertDCD;
    }

    public List<String> getInitializations()
    {
        return m_initializations;
    }

    public Modem getCaptiveModem()
    {
        return m_captiveModem;
    }

    public void setCaptiveModem(Modem captiveModem)
    {
        m_captiveModem = captiveModem;
    }

    public boolean isPlayModemSounds()
    {
        return m_playModemSounds;
    }

    public void setPlayModemSounds(boolean playModemSounds)
    {
        m_playModemSounds = playModemSounds;
    }
}
