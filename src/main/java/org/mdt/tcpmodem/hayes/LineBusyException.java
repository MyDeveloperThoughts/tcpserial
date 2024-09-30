package org.mdt.tcpmodem.hayes;

public class LineBusyException extends PortException
{
    public LineBusyException()
    {
        super("");
    }

    public LineBusyException(String error)
    {
        super(error);
    }
}
