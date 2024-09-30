package org.mdt.tcpmodem.hayes.commands;

import org.mdt.tcpmodem.hayes.ModemCore;
import org.mdt.tcpmodem.hayes.PortException;
import org.mdt.tcpmodem.exceptions.CommandException;

public class AnswerCommand extends FlagCommand
{
    public CommandResponse execute(ModemCore core) throws CommandException
    {
        switch (getLevel())
        {
            case 0:
                try
                {
                    return core.answer();
                }
                catch (PortException e)
                {
                    throw new CommandException("Answer failed", e);
                }
            case 1:
                // hang up the phone
                return core.hangup();
            default:
                return super.execute(core);

        }
    }

}