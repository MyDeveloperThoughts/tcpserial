package org.mdt.tcpmodem.hayes.commands;

import org.mdt.tcpmodem.hayes.ResponseMessage;

public class CommandResponse
{
    ResponseMessage m_message;
    String m_text;
    Throwable m_exception;

    public static CommandResponse OK = new CommandResponse(ResponseMessage.OK);

    public CommandResponse(ResponseMessage resp)
    {
        this(resp, "");
    }

    public CommandResponse(ResponseMessage resp, String text)
    {
        m_message = resp;
        m_text = text;
    }

    public ResponseMessage getResponse()
    {
        return m_message;
    }

    public String getText()
    {
        return m_text;
    }
}
