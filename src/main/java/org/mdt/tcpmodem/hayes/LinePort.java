package org.mdt.tcpmodem.hayes;

import java.io.IOException;

public interface LinePort extends ModemPort
{
    boolean isDSR();
    boolean isDCD();
    boolean isRI();
    boolean isDTR();

    void answer() throws IOException;
    void setDTR(boolean b);
    void addEventListener(LineEventListener lsnr);
    void removeEventListener(LineEventListener listener);
}
