package org.mdt.tcpmodem.hayes.commands;

import org.mdt.tcpmodem.hayes.ModemConfig;
import org.mdt.tcpmodem.hayes.ModemCore;
import org.mdt.tcpmodem.exceptions.CommandException;

public class ToneCommand extends SimpleFlagCommand
{
    public CommandResponse execute(ModemCore core) throws CommandException
    {
        core.getConfig().setDialType(ModemConfig.DIAL_TYPE_TONE);
        return CommandResponse.OK;
    }
}
