package org.mdt.tcpmodem.hayes;

public interface OptionEventHandler
{
    NVTOption newSubOption(byte code, byte[] data);
    void doReceived(OptionEvent event);    // DO
    void dontReceived(OptionEvent event);    // DONT
    void willReceived(OptionEvent event); //WILL
    void wontReceived(OptionEvent event);        //WONT
    void optionDataReceived(OptionEvent event);    // SB
}
