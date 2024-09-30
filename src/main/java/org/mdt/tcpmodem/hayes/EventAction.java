package org.mdt.tcpmodem.hayes;

public interface EventAction
{
    int getAction();
    int getDirection();
    String getContent();
    int getIterations();
    boolean isAsynchronous();

    void execute(ModemEvent event);
}
