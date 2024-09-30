package org.mdt.tcpmodem.exceptions;

public class CommandNotFoundException extends CommandException
{
    public CommandNotFoundException(char cmd)
    {
        super("Command " + cmd + " not found");
    }
}
