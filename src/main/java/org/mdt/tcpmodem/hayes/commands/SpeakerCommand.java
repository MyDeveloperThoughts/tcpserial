package org.mdt.tcpmodem.hayes.commands;

import org.mdt.tcpmodem.hayes.ModemCore;
import org.mdt.tcpmodem.exceptions.CommandException;

public class SpeakerCommand extends FlagCommand
{
    public CommandResponse execute(ModemCore core) throws CommandException
    {
        switch (getLevel())
        {
            case 0:
            case 1:
            case 2:
            case 3:
                core.getConfig().setSpeakerControl(getLevel());
                break;
            default:
                return super.execute(core);
        }
        return CommandResponse.OK;
    }
}

