package org.mdt.tcpmodem.hayes.commands;

import org.mdt.tcpmodem.hayes.LinePort;
import org.mdt.tcpmodem.hayes.ModemCore;
import org.mdt.tcpmodem.hayes.PortException;
import org.mdt.tcpmodem.exceptions.CommandException;

public class DialCommand extends AbstractCommand
{
    public static final char DIAL_DEFAULT = ' ';
    public static final char DIAL_TONE = 'T';
    public static final char DIAL_PULSE = 'P';
    public static final char DIAL_LAST = 'L';

    private char m_modifier;
    private String m_data = "";

    public char getModifier()
    {
        return m_modifier;
    }

    public String getData()
    {
        return m_data;
    }


    public int parse(byte[] data, int position, int length) throws CommandException
    {
        // skip over blanks.
        //while(iPos<iLen && data[iPos]==' ') { iPos++; }
        if (position < length)
        {
            m_modifier = Character.toUpperCase((char) data[position]);
            switch (m_modifier)
            {
                default:
                    m_modifier = DialCommand.DIAL_DEFAULT;
                    m_data = new String(data, position, length - position).trim();
                    return length;
                case 'T':
                case 'P':
                    m_data = new String(data, position + 1, length - position - 1).trim();
                    return length;
                case 'L':
                    return ++position;
            }
        }
        return length;
    }

    public CommandResponse execute(ModemCore core) throws CommandException
    {
        try
        {
            if (getModifier() == DIAL_LAST)
            {
                core.getLastNumber().sendDialResponse(core);
                return core.getLastNumber().execute(core);
            }
            else
            {
                return core.dial(this);
            }
        }
        catch (PortException e)
        {
            throw new CommandException("Connect error", e);
        }
    }

    protected void sendDialResponse(ModemCore core)
    {
        StringBuilder stringBuffer = new StringBuilder();

        if (getModifier() != DIAL_DEFAULT)
            stringBuffer.append(getModifier());

        // send number back.
        stringBuffer.append(getData());
        core.sendResponse(stringBuffer.toString().toUpperCase());
        core.sendResponse("");
    }
}

