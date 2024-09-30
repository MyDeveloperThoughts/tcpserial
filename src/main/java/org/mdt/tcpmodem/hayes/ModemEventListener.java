package org.mdt.tcpmodem.hayes;

public interface ModemEventListener extends java.util.EventListener
{
    void handleEvent(ModemEvent event);

}
