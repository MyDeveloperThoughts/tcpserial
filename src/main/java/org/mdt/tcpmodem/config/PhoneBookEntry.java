package org.mdt.tcpmodem.config;

import java.util.Objects;

public final class PhoneBookEntry
{
    private String m_number = "";

    private String m_value = "";

    public PhoneBookEntry(String number, String value)
    {
        m_number = number;
        m_value = value;
    }

    @Override
    public String toString()
    {
        return "PhoneBookEntry " + m_number + " " + m_value;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhoneBookEntry that = (PhoneBookEntry) o;
        return Objects.equals(m_number, that.m_number);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(m_number);
    }

    public String getNumber()
    {
        return m_number;
    }

    public void setNumber(String number)
    {
        m_number = number;
    }

    public String getValue()
    {
        return m_value;
    }

    public void setValue(String value)
    {
        m_value = value;
    }
}
