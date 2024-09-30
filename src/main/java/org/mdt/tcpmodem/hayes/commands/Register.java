package org.mdt.tcpmodem.hayes.commands;

import org.mdt.tcpmodem.hayes.ModemCore;
import org.mdt.tcpmodem.exceptions.CommandException;

import java.text.DecimalFormat;

public class Register extends FlagCommand
{
    public final static char ACTION_DEFAULT = 0;
    public final static char ACTION_QUERY = '?';
    public final static char ACTION_SET = '=';

    private char m_action = ACTION_DEFAULT;
    private String m_data;
    private int m_register = 0;
    protected boolean m_extended = false;

    public int parse(byte[] data, int position, int length) throws CommandException
    {
        int start;

        start = position;
        while (position < length && data[position] >= '0' && data[position] <= '9')
        {
            position++;
        }
        // get register
        try
        {
            setRegister(Integer.parseInt(new String(data, start, position - start)));
        }
        catch (Exception e)
        {
            // assume register 0
        }
        // skip over spaces.
        while (position < length && data[position] == ' ')
        {
            position++;
        }
        if (position < length)
        {
            switch (data[position])
            {
                case '=':
                    setAction(Register.ACTION_SET);
                    position++;
                    break;
                case '?':
                    setAction(Register.ACTION_QUERY);
                    position++;
                    break;
            }
            if (getAction() == ACTION_SET)
            {
                // skip spaces.
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
            }
        }
        else
        {
            // setting default register
        }
        return position;
    }

    protected void setValue(String data)
    {
        m_data = data;
    }

    protected void setRegister(int i)
    {
        m_register = i;

    }

    protected void setAction(char action)
    {
        m_action = action;
    }

    public CommandResponse execute(ModemCore core) throws CommandException
    {
        switch (getAction())
        {
            case ACTION_SET:
                core.getConfig().setRegister(getRegister(), getIntValue());
                break;
            case ACTION_QUERY:
                DecimalFormat df = new DecimalFormat("000");
                core.sendResponse(df.format(core.getConfig().getRegister(getRegister())));
                break;
            case ACTION_DEFAULT:
                core.getConfig().setDefaultRegister(getRegister());
                break;
        }
        return CommandResponse.OK;

    }

    public int getIntValue() throws CommandException
    {
        try
        {
            return Integer.parseInt(getValue());
        }
        catch (NumberFormatException e)
        {
            throw new CommandException("Invalid value for Register " + getRegister());
        }
    }

    public String getValue()
    {
        return m_data;
    }

    public int getRegister()
    {
        return m_register;
    }

    public char getAction()
    {
        return m_action;
    }
}

