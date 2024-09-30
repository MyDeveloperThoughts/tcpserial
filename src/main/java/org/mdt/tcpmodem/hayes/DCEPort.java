package org.mdt.tcpmodem.hayes;

public interface DCEPort extends ModemPort
{
    void setDCD(boolean b);
    boolean isDCD();
    boolean isDTR();
    void setRI(boolean b);
    boolean isRI();
    void setDSR(boolean b);
    boolean isDSR();
    void addEventListener(DCEEventListener lsnr);
    void removeEventListener(DCEEventListener listener);
}