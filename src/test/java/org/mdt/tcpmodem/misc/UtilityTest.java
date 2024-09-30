package org.mdt.tcpmodem.misc;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class UtilityTest
{
    @Test
    void getString()
    {
        assertEquals("",            Utility.getString(null));
        assertEquals("Test",        Utility.getString(" Test "));
        assertEquals("Test",        Utility.getString(" Test"));
        assertEquals("Test",        Utility.getString("Test   "));
        assertEquals("Hello World", Utility.getString("Hello World"));
    }

    @Test
    void dumpHex()
    {
        byte[] testBytes = "This code creates an easy to read hex dump that is very useful for debugging.\0\t\t".getBytes(StandardCharsets.UTF_8);

        String expectedResults = """
        0000| 63 72 65 61 74 65 73 20  61 6E 20 65 61 73 79    |creates an easy |""";

        String actual = Utility.dumpHex(testBytes,10,15);
        assertEquals(expectedResults, actual);
    }

    @Test
    void dumpHex2()
    {
        byte[] testBytes = "This code creates an easy to read hex dump that is very useful for debugging.\0\t\t".getBytes(StandardCharsets.UTF_8);

        String expectedResults = """
        0000| 54 68 69 73 20 63 6F 64  65 20 63 72 65 61 74 65 |This code create|
        0010| 73 20 61 6E 20 65 61 73  79 20 74 6F 20 72 65 61 |s an easy to rea|
        0020| 64 20 68 65 78 20 64 75  6D 70 20 74 68 61 74 20 |d hex dump that |
        0030| 69 73 20 76 65 72 79 20  75 73 65 66 75 6C 20 66 |is very useful f|
        0040| 6F 72 20 64 65 62 75 67  67 69 6E 67 2E 00 09 09 |or debugging....|""";

        String actual = Utility.dumpHex(testBytes);
        assertEquals(expectedResults, actual);
    }

    @Test
    void convertToInteger()
    {
        assertEquals(Utility.convertToInteger("1"), 1);
        assertEquals(Utility.convertToInteger("1,000"), 0);
        assertEquals(Utility.convertToInteger("1.000"), 0);
        assertEquals(Utility.convertToInteger("2345325"), 2345325);
        assertEquals(Utility.convertToInteger("-2345325"), -2345325);
        assertEquals(Utility.convertToInteger("SDFKJLSDLKFJ"), 0);
        assertEquals(Utility.convertToInteger("123AB12"), 0);
        assertEquals(Utility.convertToInteger(""), 0);
        assertEquals(Utility.convertToInteger(" "), 0);
        assertEquals(Utility.convertToInteger(null), 0);

        assertNotEquals(Utility.convertToInteger("1"), 123123);
        assertNotEquals(Utility.convertToInteger("2345325"), -2345325);
        assertNotEquals(Utility.convertToInteger("-2345325"), 2345325);
    }
}
