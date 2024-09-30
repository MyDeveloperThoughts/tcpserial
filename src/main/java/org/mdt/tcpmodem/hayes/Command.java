package org.mdt.tcpmodem.hayes;

import org.mdt.tcpmodem.exceptions.CommandException;
import org.mdt.tcpmodem.hayes.commands.CommandResponse;

public interface Command
{
    char TYPE_NORMAL = ' ';
    char TYPE_EXTENDED = '&';
    char TYPE_PROP_PERCENT = '%';
    char TYPE_PROP_BACKSLASH = '\\';
    char TYPE_PROP_COLON = ':';
    char TYPE_PROP_MINUS = '-';

    void init(char type, char cmd);
    char getFlag();
    char getType();

    int parse(byte[] data, int pos, int len) throws CommandException;
    CommandResponse execute(ModemCore core) throws CommandException;
}
