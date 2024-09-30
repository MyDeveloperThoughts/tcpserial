package org.mdt.tcpmodem.hayes.commands;

import org.mdt.tcpmodem.hayes.ModemCore;
import org.mdt.tcpmodem.hayes.PortException;
import org.mdt.tcpmodem.exceptions.CommandException;

public class HangupCommand extends FlagCommand
{
    public CommandResponse execute(ModemCore core) throws CommandException
    {
        switch (getLevel())
        {
            case 0:
                // hangup
                return core.hangup();
            case 1:
                try
                {
                    return core.answer();
                }
                catch (PortException e)
                {
                    throw new CommandException("Answer failed", e);
                }
            default:
                return super.execute(core);
        }
    }
}

