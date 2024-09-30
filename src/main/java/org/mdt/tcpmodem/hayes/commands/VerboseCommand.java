package org.mdt.tcpmodem.hayes.commands;

import org.mdt.tcpmodem.hayes.ModemCore;
import org.mdt.tcpmodem.exceptions.CommandException;

public class VerboseCommand extends FlagCommand
{
    public CommandResponse execute(ModemCore core) throws CommandException
    {
        switch (getLevel())
        {
            case 0:
                core.getConfig().setVerbose(false);
                break;
            case 1:
                core.getConfig().setVerbose(true);
                break;
            default:
                return super.execute(core);
        }
        return CommandResponse.OK;
    }
}

