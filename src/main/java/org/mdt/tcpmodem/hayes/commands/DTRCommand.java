package org.mdt.tcpmodem.hayes.commands;


import org.mdt.tcpmodem.hayes.ModemConfig;
import org.mdt.tcpmodem.hayes.ModemCore;
import org.mdt.tcpmodem.exceptions.CommandException;

public class DTRCommand extends FlagCommand
{
    public CommandResponse execute(ModemCore core) throws CommandException
    {
        ModemConfig modemConfig = core.getConfig();
        switch (getLevel())
        {
            case 0:
            case 1:
            case 2:
            case 3:
                modemConfig.setDTRAction(getLevel());
                break;
            default:
                return super.execute(core);
        }
        return CommandResponse.OK;
    }
}
