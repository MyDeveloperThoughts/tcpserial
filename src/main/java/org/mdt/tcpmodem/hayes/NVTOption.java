package org.mdt.tcpmodem.hayes;

public class NVTOption
{
    public static final byte IAC = (byte) 255;
    public static final byte DO = (byte) 253;
    public static final byte WONT = (byte) 252;
    public static final byte WILL = (byte) 251;
    public static final byte DONT = (byte) 254;
    public static final byte SE = (byte) 240;
    public static final byte NOP = (byte) 241;
    public static final byte DM = (byte) 242;
    public static final byte SB = (byte) 250;

    public static final byte OPT_TRANSMIT_BINARY = (byte) 0;
    public static final byte OPT_ECHO = (byte) 1;
    public static final byte OPT_SUPPRESS_GO_AHEAD = (byte) 3;
    public static final byte OPT_STATUS = (byte) 5;
    public static final byte OPT_RCTE = (byte) 7;
    public static final byte OPT_TIMING_MARK = (byte) 6;
    public static final byte OPT_NAOCRD = (byte) 10;
    public static final byte OPT_TERMINAL_TYPE = (byte) 24;
    public static final byte OPT_NAWS = (byte) 31;
    public static final byte OPT_TERMINAL_SPEED = (byte) 32;
    public static final byte OPT_TOGGLE_FLOW_CONTROL = (byte) 33;
    public static final byte OPT_LINEMODE = (byte) 34;
    public static final byte OPT_X_DISPLAY_LOCATION = (byte) 35;
    public static final byte OPT_ENVIRON = (byte) 36;
    public static final byte OPT_NEW_ENVIRON = (byte) 39;

    private byte m_code;

    public NVTOption(byte code)
    {
        m_code = code;
    }

    public byte getOptionCode()
    {
        return m_code;
    }

    public byte[] getOptionData()
    {
        return null;
    }

    public String toString()
    {
        return "OPTION " + m_code;
    }

}
