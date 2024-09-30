package org.mdt.tcpmodem.config;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PhoneBookLogicTest
{
    @Test
    public void buildPhoneBook()
    {
        List<PhoneBookEntry> testEntries = List.of(
            new PhoneBookEntry("madworld", "murdworld.bounceme.net:23"),
            new PhoneBookEntry("addixion", "addixion.hopto.org:23"),
            new PhoneBookEntry("antidote", "antidote.hopto.org:23"),
            new PhoneBookEntry("carbonek", "65.40.213.24:23"),
            new PhoneBookEntry("dragonseye", "dragonseye.dyndns.org:6400"),
            new PhoneBookEntry("elmstreet", "elmstreet.dyndns.org:23"),
            new PhoneBookEntry("forgottenrealms", "forgottenrealmsbbs.org:23"),
            new PhoneBookEntry("jammingsignal", "bbs.jammingsignal.com:23"),
            new PhoneBookEntry("hidden", "the-hidden.hopto.org:23"),
            new PhoneBookEntry("laststand", "laststandbbs.net:23"),
            new PhoneBookEntry("lostcaverns", "lostcavernsbbs.dyndns.org:6001"),
            new PhoneBookEntry("lostcaverns1", "lostcavernsbbs.dyndns.org:6001"),
            new PhoneBookEntry("Lostcaverns2", "lostcavernsbbs.dyndns.org:6002"),
            new PhoneBookEntry("renaissancecity", "renaissancecitybbs.dyndns.tv:23"),
            new PhoneBookEntry("RKbbs", "rkbbs.net:23"));

        Properties testProperties = new Properties();
        PhoneBookLogic.buildPhoneBook(testEntries, testProperties);

        assertEquals(testEntries.size(), testProperties.size());
        for(PhoneBookEntry entry : testEntries)
            assertEquals(testProperties.get(entry.getNumber().toLowerCase()), entry.getValue());
    }
}
