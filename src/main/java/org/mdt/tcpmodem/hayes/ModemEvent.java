package org.mdt.tcpmodem.hayes;

import java.util.EventObject;

public class ModemEvent extends EventObject
{
    private int m_type;

    public static final int OFF_HOOK = 1;
    public static final int ANSWER = 2;
    public static final int DIAL = 3;
    public static final int WAITING_FOR_CALL = 4;
    public static final int RING = 5;
    public static final int CONNECT = 6;
    public static final int HANGUP = 7;
    public static final int RESPONSE_BUSY = 8;
    public static final int RESPONSE_NO_ANSWER = 9;
    public static final int ON_HOOK = 11;
    public static final int CMD_MODE = 12;
    public static final int DATA_MODE = 13;
    public static final int RESPONSE_ERROR = 10;
    public static final int PRE_ANSWER = 14;
    public static final int PRE_CONNECT = 15;

    public int getType()
    {
        return m_type;
    }

    public ModemEvent(Object arg0, int type)
    {
        super(arg0);
        m_type = type;
    }

}
