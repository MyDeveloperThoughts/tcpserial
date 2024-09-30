package org.mdt.tcpmodem.exceptions;

import org.mdt.tcpmodem.hayes.PortException;

public class LineNotAnsweringException extends PortException
{
    public LineNotAnsweringException()
    {
        super("");
    }

    public LineNotAnsweringException(String error)
    {
        super(error);
    }

    public LineNotAnsweringException(String error, Throwable e)
    {
        super(error, e);
    }
}
