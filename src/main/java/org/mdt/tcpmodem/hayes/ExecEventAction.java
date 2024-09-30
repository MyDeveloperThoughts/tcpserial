package org.mdt.tcpmodem.hayes;

public class ExecEventAction extends AbstractEventAction
{
    public ExecEventAction(int direction, int action, String data, int iterations, boolean aynchronous)
    {
        super(direction, action, data, iterations, aynchronous);
    }

    public void execute(ModemEvent event)
    {

    }
}
