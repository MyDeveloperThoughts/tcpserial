package org.mdt.tcpmodem.config;

import java.util.List;
import java.util.Properties;

public class PhoneBookLogic
{
    public static void buildPhoneBook(List<PhoneBookEntry> phoneBookEntries, Properties properties)
    {
        for(PhoneBookEntry entry : phoneBookEntries)
            properties.setProperty(entry.getNumber().trim().toLowerCase(), entry.getValue());
    }
}
