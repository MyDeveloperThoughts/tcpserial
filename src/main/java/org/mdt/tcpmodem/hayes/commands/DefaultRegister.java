package org.mdt.tcpmodem.hayes.commands;


import org.mdt.tcpmodem.hayes.ModemCore;
import org.mdt.tcpmodem.exceptions.CommandException;
import org.mdt.tcpmodem.exceptions.CommandNotFoundException;

public class DefaultRegister extends Register
{
    public int parse(byte[] data, int position, int length) throws CommandException
    {
        int start;
        switch (getFlag())
        {
            case ACTION_SET:
                setAction(ACTION_SET);
                while (position < length && data[position] == ' ')
                {
                    position++;
                }
                if (position < length)
                {
                    start = position;
                    if (m_extended)
                    {
                        while (position < length && data[position] == ' ')
                        {
                            position++;
                        }
                    }
                    else
                    {
                        while (position < length && data[position] >= '0' && data[position] <= '9')
                        {
                            position++;
                        }
                    }
                    setValue(new String(data, start, position - start));
                }
                break;
            case ACTION_QUERY:
                setAction(ACTION_QUERY);
                break;
            default:
                throw new CommandNotFoundException(this.getFlag());
        }
        return position;
    }

    public CommandResponse execute(ModemCore core) throws CommandException
    {
        setRegister(core.getConfig().getDefaultRegister());
        return super.execute(core);
    }


}
