package org.mdt.tcpmodem.exceptions;


public class GenericException extends Exception
{
    private Throwable m_exception;

    public GenericException()
    {
        super();
    }

    public GenericException(String error)
    {
        super(error);
    }

    public GenericException(String error, Throwable t)
    {
        super(error);
        m_exception = t;
    }


}
