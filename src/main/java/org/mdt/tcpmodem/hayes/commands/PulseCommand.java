package org.mdt.tcpmodem.hayes.commands;

import org.mdt.tcpmodem.exceptions.CommandException;
import org.mdt.tcpmodem.hayes.ModemConfig;
import org.mdt.tcpmodem.hayes.ModemCore;

public class PulseCommand extends SimpleFlagCommand
{
    public CommandResponse execute(ModemCore core) throws CommandException
    {
        core.getConfig().setDialType(ModemConfig.DIAL_TYPE_PULSE);
        return CommandResponse.OK;
    }
}
