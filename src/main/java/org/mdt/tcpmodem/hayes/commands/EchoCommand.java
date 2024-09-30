package org.mdt.tcpmodem.hayes.commands;

import org.mdt.tcpmodem.hayes.ModemCore;
import org.mdt.tcpmodem.exceptions.CommandException;

public class EchoCommand extends FlagCommand
{
    public CommandResponse execute(ModemCore core) throws CommandException
    {
        switch (getLevel())
        {
            case 0:
                // no echo
                core.getConfig().setEcho(false);
                break;
            case 1:
                // echo
                core.getConfig().setEcho(true);
                break;
            default:
                return super.execute(core);
        }
        return CommandResponse.OK;
    }
}
