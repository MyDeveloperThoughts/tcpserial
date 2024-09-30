package org.mdt.tcpmodem.hayes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface ModemPort
{
    public static final int BPS_UNKNOWN = 0;
    public static final int BPS_300 = 300;
    public static final int BPS_0600 = 600;
    public static final int BPS_1200 = 1200;
    public static final int BPS_2400 = 2400;
    public static final int BPS_4800 = 4800;
    public static final int BPS_7200 = 7200;
    public static final int BPS_9600 = 9600;
    public static final int BPS_12000 = 12000;
    public static final int BPS_14400 = 14400;
    public static final int BPS_19200 = 19200;
    public static final int BPS_57600 = 57600;
    public static final int BPS_38400 = 38400;
    public static final int BPS_115200 = 115200;
    public static final int BPS_230400 = 230400;
    void setFlowControl(int control);
    void start();
    InputStream getInputStream() throws IOException;
    OutputStream getOutputStream() throws IOException;
    int getSpeed();
}