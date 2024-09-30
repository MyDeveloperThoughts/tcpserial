package org.mdt.tcpmodem.hayes.commands;


import org.mdt.tcpmodem.hayes.ModemCore;
import org.mdt.tcpmodem.exceptions.CommandException;

public class QuietCommand extends FlagCommand
{
    public CommandResponse execute(ModemCore core) throws CommandException
    {
        switch (getLevel())
        {
            case 0:
                // send responses
                core.getConfig().setQuiet(false);
                break;
            case 1:
                // quiet
                core.getConfig().setQuiet(true);
                break;
            default:
                return super.execute(core);
        }
        return CommandResponse.OK;
    }
}
