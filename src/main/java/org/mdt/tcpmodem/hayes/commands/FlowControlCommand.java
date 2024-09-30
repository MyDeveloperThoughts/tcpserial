package org.mdt.tcpmodem.hayes.commands;


import org.mdt.tcpmodem.hayes.ModemConfig;
import org.mdt.tcpmodem.hayes.ModemCore;
import org.mdt.tcpmodem.exceptions.CommandException;

public class FlowControlCommand extends FlagCommand
{
    public CommandResponse execute(ModemCore core) throws CommandException
    {
        switch (getLevel())
        {
            case 0:
                // no flow control
                core.setFlowControl(ModemConfig.FLOWCONTROL_NONE);
                break;
            case 3:
                // rts
                core.setFlowControl(ModemConfig.FLOWCONTROL_RTSCTS_IN | ModemConfig.FLOWCONTROL_RTSCTS_OUT);
                break;
            case 4:
                // Transparent XON/XOFF
                core.setFlowControl(ModemConfig.FLOWCONTROL_XONXOFF_IN | ModemConfig.FLOWCONTROL_XONXOFF_OUT);
                break;
            case 5:
                // XON
                core.setFlowControl(ModemConfig.FLOWCONTROL_XONXOFF_IN | ModemConfig.FLOWCONTROL_XONXOFF_OUT);
                break;
            case 6:
                // XON
                core.setFlowControl(ModemConfig.FLOWCONTROL_RTSCTS_IN | ModemConfig.FLOWCONTROL_RTSCTS_OUT | ModemConfig.FLOWCONTROL_XONXOFF_IN | ModemConfig.FLOWCONTROL_XONXOFF_OUT);
                break;
            default:
                return super.execute(core);
        }
        return CommandResponse.OK;
    }
}
