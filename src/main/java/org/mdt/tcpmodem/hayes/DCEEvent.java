package org.mdt.tcpmodem.hayes;

public class DCEEvent extends SerialEvent
{
    //public static final int BI=10;
    public static final int CTS = 3;
    public static final int DATA_AVAILABLE = 1;
    public static final int DTR = 4;
    public static final int FE = 9;
    public static final int OE = 7;
    public static final int OUTPUT_BUFFER_EMPTY = 2;
    public static final int PE = 8;

    public DCEEvent(ModemPort port, int type, boolean oldValue, boolean newValue)
    {
        super(port, type, oldValue, newValue);
    }
}