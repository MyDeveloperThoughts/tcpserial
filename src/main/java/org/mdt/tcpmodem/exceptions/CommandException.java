package org.mdt.tcpmodem.exceptions;

public class CommandException extends GenericException
{
    public CommandException(String error)
    {
        super(error);
    }

    public CommandException(String error, Throwable t)
    {
        super(error, t);
    }
}
