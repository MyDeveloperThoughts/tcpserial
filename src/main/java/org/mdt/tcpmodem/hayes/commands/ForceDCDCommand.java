package org.mdt.tcpmodem.hayes.commands;

import org.mdt.tcpmodem.hayes.ModemConfig;
import org.mdt.tcpmodem.hayes.ModemCore;
import org.mdt.tcpmodem.exceptions.CommandException;

public class ForceDCDCommand extends FlagCommand
{
    public CommandResponse execute(ModemCore core) throws CommandException
    {
        ModemConfig cfg = core.getConfig();
        switch (getLevel())
        {
            case 0:
                cfg.setDCDForced(true);
                break;
            case 1:
                cfg.setDCDForced(false);
                break;
            default:
                return super.execute(core);
        }
        return CommandResponse.OK;
    }
}
