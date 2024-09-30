package org.mdt.tcpmodem.hayes;

import org.mdt.tcpmodem.exceptions.GenericException;

public class PortException extends GenericException
{
    public PortException(String error)
    {
        super(error);
    }

    public PortException(String error, Throwable t)
    {
        super(error, t);
    }
}
