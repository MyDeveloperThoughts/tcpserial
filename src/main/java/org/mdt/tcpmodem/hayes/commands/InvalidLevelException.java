package org.mdt.tcpmodem.hayes.commands;

import org.mdt.tcpmodem.exceptions.CommandException;

public class InvalidLevelException extends CommandException
{
    public InvalidLevelException(FlagCommand cmd)
    {
        super("Cannot set command " + cmd.getFlag() + " to level " + cmd.getLevel());
    }

}

