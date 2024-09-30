package org.mdt.tcpmodem.misc;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Logging
{
    public static void initializeLogFormatter()
    {
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        rootLogger.getHandlers()[0].setFormatter(new LogRecordFormatter());
    }

    /**
     * Override the default log level (which is INFO).
     * Java.util.Logging Log Levels are:
     * SEVERE
     * WARNING
     * INFO     (INFO up to SEVERE are logged by default)
     * CONFIG
     * FINE
     * @param newLoggingLevel New Logging Level
     */
    public static void setLogLevel(Level newLoggingLevel)
    {
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        rootLogger.setLevel(newLoggingLevel);
        for(Handler handler : rootLogger.getHandlers())
            handler.setLevel(newLoggingLevel);
    }
}
