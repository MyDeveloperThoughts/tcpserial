package org.mdt.tcpmodem.misc;

import org.mdt.tcpmodem.Main;

import javax.xml.stream.XMLInputFactory;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

public class Utility
{
    private static final Logger m_log = Logger.getLogger(Main.class.getName());

    public static String dumpHex(byte[] b, int start, int length)
    {
        StringBuilder sb = new StringBuilder();
        char[] text = new char[16];
        int pos = start;
        int i = 0;
        int j;
        while (i < length)
        {
            j = i & 0x0f;
            String hexTable = "0123456789ABCDEF";
            if (j == 0)
            {
                sb.append(hexTable.charAt(i >> 12 & 0x0f));
                sb.append(hexTable.charAt(i >> 8 & 0x0f));
                sb.append(hexTable.charAt(i >> 4 & 0x0f));
                sb.append(hexTable.charAt(i & 0x0f));
                sb.append("|");
            } else if (j == 8)
            {
                sb.append(" ");
            }
            sb.append(" ");
            // print out.
            sb.append(hexTable.charAt(b[pos] >> 4 & 0x0f));
            sb.append(hexTable.charAt(b[pos] & 0x0f));
            if (b[pos] > 31 && b[pos] < 127)
            {
                text[j] = (char) b[pos];
            } else
                text[j] = '.';
            if (j == 15)
            {
                sb.append(" |");
                sb.append(text);
                sb.append("|\n");
            }
            i++;
            pos++;
        }
        // handle last line.
        if ((i & 0x0f) != 0)
        {
            // finish line
            for (j = i & 0x0f; j < 16; j++)
            {
                if (j == 8)
                    sb.append(" ");
                text[j] = ' ';
                sb.append("   ");
            }
            sb.append(" |");
            sb.append(text);
            sb.append("|");
        }

        if (sb.charAt(sb.length() - 1) == '\n')
            sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }

    public static String dumpHex(byte[] b)
    {
        return dumpHex(b, 0, b.length);
    }

    public static void copyData(InputStream is, OutputStream os) throws IOException
    {
        BufferedInputStream b;

        b = new BufferedInputStream(is);
        byte[] data = new byte[1024];
        int len;

        while ((len = b.read(data)) > -1)
        {
            os.write(data, 0, len);
        }
    }

    public static XMLInputFactory getStaxReaderFactory()
    {
        XMLInputFactory factory = XMLInputFactory.newDefaultFactory();
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        return factory;
    }

    public static String getString(Object object)
    {
        return object == null ? "" : object.toString().trim();
    }

    public static int convertToInteger(String number)
    {
        if (number == null || number.isEmpty()) return 0;
        number = number.trim();
        if (number.isEmpty()) return 0;

        try
        {
            return Integer.parseInt(number);
        } catch (NumberFormatException e)
        {
            return 0;
        }
    }

    /**
     * Given a class and a path to a filename.  This will attempt to resolve it and return the fully qualified filename.
     * For example: Given the class org.mdt.tcpmodem.hayes.AudioEventAction and audio/dial_tone this will return
     * c:/.etc./audio/dialtone
     *
     * @param clazz    class of the caller
     * @param audioFilename filename relative to our resources root.
     * @return Path to the filename.  null if not found.
     */
    public static Path getResourceAsPath(String audioFilename)
    {
        Path testFile = Path.of("audio", audioFilename).toAbsolutePath();
        if (Files.exists(testFile) && Files.isRegularFile(testFile))
            return testFile;
        return null;
    }
}
