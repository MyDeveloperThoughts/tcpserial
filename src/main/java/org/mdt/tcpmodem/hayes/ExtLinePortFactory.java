package org.mdt.tcpmodem.hayes;

import org.mdt.tcpmodem.hayes.commands.DialCommand;
import org.mdt.tcpmodem.exceptions.LineNotAnsweringException;

public class ExtLinePortFactory implements LinePortFactory
{
    LinePort m_port = null;

    public void setCaptiveLine(LinePort port)
    {
        m_port = port;
    }

    public LinePort createLinePort(DialCommand cmd) throws PortException
    {
        return new TCPPort(cmd.getData());
    }
}
