package org.mdt.tcpmodem.hayes;

public class HostAddress
{
    private String m_host;
    private int m_port;

    public HostAddress(String address)
    {
        int pos = address.indexOf(":");

        if (pos > -1)
        {
            // trim off spaces if name : port
            m_host = address.substring(0, pos).trim();
            m_port = Integer.parseInt(address.substring(pos + 1).trim());
        }
        else
        {
            throw new NumberFormatException("No port specified");
        }
    }

    public int getPort()
    {
        return m_port;
    }

    public String getHost()
    {
        return m_host;
    }
}
