package org.mdt.tcpmodem.hayes;

import org.mdt.tcpmodem.hayes.commands.DialCommand;
import org.mdt.tcpmodem.exceptions.LineNotAnsweringException;

public interface LinePortFactory
{
    LinePort createLinePort(DialCommand cmd) throws PortException;
}
