package org.mdt.tcpmodem.hayes.commands;

import org.mdt.tcpmodem.hayes.Command;

public abstract class AbstractCommand implements Command
{
    private char m_command;
    private char m_type;

    public void init(char type, char cmd)
    {
        m_command = cmd;
        m_type = type;
    }

    public char getFlag()
    {
        return m_command;
    }

    public char getType()
    {
        return m_type;
    }
}
