package org.mdt.tcpmodem.hayes.commands;

import org.mdt.tcpmodem.hayes.ModemConfig;
import org.mdt.tcpmodem.hayes.ModemCore;
import org.mdt.tcpmodem.exceptions.CommandException;

public class ProtocolCommand extends FlagCommand
{
    public CommandResponse execute(ModemCore core) throws CommandException
    {
        switch (getLevel())
        {
            case 0:
                core.getConfig().set1200Protocol(ModemConfig.PROTOCOL_V22);
                break;
            case 1:
                core.getConfig().set1200Protocol(ModemConfig.PROTOCOL_BELL_212A);
                break;
            default:
                return super.execute(core);
        }
        return CommandResponse.OK;
    }
}

