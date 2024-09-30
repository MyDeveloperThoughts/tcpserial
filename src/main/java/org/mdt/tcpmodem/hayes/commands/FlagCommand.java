package org.mdt.tcpmodem.hayes.commands;

import org.mdt.tcpmodem.hayes.ModemCore;
import org.mdt.tcpmodem.exceptions.CommandException;

public abstract class FlagCommand extends AbstractCommand
{
    private int m_level = 0;

    public int parse(byte[] data, int position, int length) throws CommandException
    {
        int start = position;

        while (position < length && data[position] >= '0' && data[position] <= '9')
        {
            position++;
        }
        if (position != start)
        {
            try
            {
                m_level = Integer.parseInt(new String(data, start, position - start));
            }
            catch (Exception e)
            {
                // cannot happen
            }
        }

        return position;
    }

    public CommandResponse execute(ModemCore core) throws CommandException
    {
        throw new InvalidLevelException(this);
    }

    public int getLevel()
    {
        return m_level;
    }
}

