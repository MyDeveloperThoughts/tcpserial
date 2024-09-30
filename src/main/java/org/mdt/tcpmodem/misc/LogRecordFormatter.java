package org.mdt.tcpmodem.misc;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogRecordFormatter extends Formatter
{
    private static final DateTimeFormatter m_dateTimeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm:s a")
            .withZone(ZoneId.systemDefault());

    @Override
    public String format(LogRecord record)
    {
        String shortClassName = getShortClassName(record.getSourceClassName());

        String fullMessageText = record.getMessage();
        fullMessageText = fullMessageText.replaceAll("\r\n", "\n");
        if (fullMessageText.endsWith("\n")) fullMessageText = fullMessageText.substring(0, fullMessageText.length() - 1);

        String[] messages = fullMessageText.split("\n");
        StringBuilder formattedMessage = new StringBuilder(fullMessageText.length());
        for(String message : messages)
        {
            String temp = String.format("%-25s %-7s %-27s %s\n",
                    m_dateTimeFormatter.format(record.getInstant()),
                    record.getLevel(),
                    shortClassName +":" + record.getSourceMethodName(),
                    message);
            formattedMessage.append(temp);
        }

        return formattedMessage.toString();
    }

    public static String getShortClassName(String sourceClassName)
    {
        int lastIndexOfDot = sourceClassName.lastIndexOf('.') + 1;

        return sourceClassName.substring(lastIndexOfDot);
    }

}
