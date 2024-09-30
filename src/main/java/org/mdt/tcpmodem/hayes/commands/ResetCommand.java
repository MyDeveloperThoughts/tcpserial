package org.mdt.tcpmodem.hayes.commands;

import org.mdt.tcpmodem.hayes.ModemCore;
import org.mdt.tcpmodem.exceptions.CommandException;

public class ResetCommand extends FlagCommand
{
    public CommandResponse execute(ModemCore core) throws CommandException
    {
        switch (getLevel())
        {
            case 0:
                if (core.getConfig().getConfig(0) != null)
                    core.setConfig(core.getConfig().getConfig(0));
                core.reset();
                break;
            case 1:
                if (core.getConfig().getConfig(1) != null)
                    core.setConfig(core.getConfig().getConfig(1));
                core.reset();
                break;
            default:
                return super.execute(core);
        }
        return CommandResponse.OK;
    }
}
