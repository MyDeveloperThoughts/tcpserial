package org.mdt.tcpmodem.hayes.commands;

import org.mdt.tcpmodem.exceptions.CommandException;

public abstract class SimpleFlagCommand extends FlagCommand
{
    public int parse(byte[] data, int position, int length) throws CommandException
    {
        // no more to parse here.
        return position;
    }
}
