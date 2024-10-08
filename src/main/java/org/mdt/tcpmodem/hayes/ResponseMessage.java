package org.mdt.tcpmodem.hayes;

public class ResponseMessage
{
    public static ResponseMessage OK = new ResponseMessage(0, "OK");
    public static ResponseMessage CONNECT = new ResponseMessage(1, "CONNECT");
    public static ResponseMessage RING = new ResponseMessage(2, "RING");
    public static ResponseMessage NO_CARRIER = new ResponseMessage(3, "NO CARRIER");
    public static ResponseMessage ERROR = new ResponseMessage(4, "ERROR");
    public static ResponseMessage CONNECT_1200 = new ResponseMessage(5, "CONNECT 1200");
    public static ResponseMessage NO_DIALTONE = new ResponseMessage(6, "NO DIALTONE");
    public static ResponseMessage BUSY = new ResponseMessage(7, "BUSY");
    public static ResponseMessage NO_ANSWER = new ResponseMessage(8, "NO ANSWER");
    public static ResponseMessage CONNECT_0600 = new ResponseMessage(9, "CONNECT 0600");
    public static ResponseMessage CONNECT_2400 = new ResponseMessage(10, "CONNECT 2400");
    public static ResponseMessage CONNECT_4800 = new ResponseMessage(11, "CONNECT 4800");
    public static ResponseMessage CONNECT_9600 = new ResponseMessage(12, "CONNECT 9600");
    public static ResponseMessage CONNECT_7200 = new ResponseMessage(13, "CONNECT 7200");
    public static ResponseMessage CONNECT_12000 = new ResponseMessage(14, "CONNECT 12000");
    public static ResponseMessage CONNECT_14400 = new ResponseMessage(15, "CONNECT 14400");
    public static ResponseMessage CONNECT_19200 = new ResponseMessage(16, "CONNECT 19200");
    public static ResponseMessage CONNECT_38400 = new ResponseMessage(17, "CONNECT 38400");
    public static ResponseMessage CONNECT_57600 = new ResponseMessage(18, "CONNECT 57600");
    public static ResponseMessage CONNECT_115200 = new ResponseMessage(19, "CONNECT 115200");
    public static ResponseMessage CONNECT_230400 = new ResponseMessage(20, "CONNECT 230400");
    private int m_number;
    private String m_text;

    private ResponseMessage(int num, String text)
    {
        m_number = num;
        m_text = text;
    }

    public String getText(int level)
    {
        return m_text;
    }

    public int getCode()
    {
        return m_number;
    }

    public static ResponseMessage getConnectResponse(int speed, int level)
    {
        if (level == 0)
        {
            return CONNECT;
        }
        else
        {
            switch (speed)
            {
                case ModemPort.BPS_0600:    return CONNECT_0600;
                case ModemPort.BPS_1200:    return CONNECT_1200;
                case ModemPort.BPS_2400:    return CONNECT_2400;
                case ModemPort.BPS_4800:    return CONNECT_4800;
                case ModemPort.BPS_7200:    return CONNECT_7200;
                case ModemPort.BPS_9600:    return CONNECT_9600;
                case ModemPort.BPS_12000:   return CONNECT_12000;
                case ModemPort.BPS_14400:   return CONNECT_14400;
                case ModemPort.BPS_19200:   return CONNECT_19200;
                case ModemPort.BPS_38400:   return CONNECT_38400;
                case ModemPort.BPS_57600:   return CONNECT_57600;
                case ModemPort.BPS_115200:  return CONNECT_115200;
                case ModemPort.BPS_230400:  return CONNECT_230400;
                default:                    return CONNECT;
            }
        }
    }
}
