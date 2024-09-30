package org.mdt.tcpmodem.hayes;

public interface LineEventListener extends java.util.EventListener
{
    void lineEvent(LineEvent event);
}
