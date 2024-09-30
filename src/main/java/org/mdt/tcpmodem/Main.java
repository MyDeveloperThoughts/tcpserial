package org.mdt.tcpmodem;

import org.mdt.tcpmodem.config.Config;
import org.mdt.tcpmodem.config.ConfigLoader;
import org.mdt.tcpmodem.hayes.ModemPoolThread;
import org.mdt.tcpmodem.misc.Logging;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Main extends Thread
{
    private static final Logger m_log = Logger.getLogger(Main.class.getName());
    private static final List<ModemPoolThread> m_modemPoolThreads = new ArrayList<>();

    /**
     * Entry point to the program.
     *
     * @param arguments -logXXX to change the default log level. -logSevere, -logWarning, -logInfo, -logConfig, -logFine
     */
    public static void main(String[] arguments)
    {
        Logging.initializeLogFormatter();
        Version.showStartupMessage();
        processCommandLineArguments(arguments);

        Path configFile = Path.of("config.xml").toAbsolutePath();
        if (!ConfigLoader.loadConfig(configFile)) return;

        try
        {
            Object o = new Object();
            ModemPoolThread pool;
            for (int i = 0, size = Config.getModemPools().size(); i < size; i++)
            {
                pool = new ModemPoolThread(Config.getModemPools().get(i));
                m_modemPoolThreads.add(pool);
            }

            synchronized (o)
            {
                o.wait();
            }
        }
        catch (Exception e)
        {
            m_log.log(Level.SEVERE, e.getMessage(), e);
        }
        m_log.info("TCPSerial shutting down.");
    }

    private static void processCommandLineArguments(String[] arguments)
    {
        Level currentLogLevel = Level.INFO;

        for (String argument : arguments)
        {
            if (argument.startsWith("-log") && argument.length() > 4)
            {
                String newLevel = argument.substring(4).toLowerCase();
                currentLogLevel = switch (newLevel)
                {
                    case "severe" -> Level.SEVERE;
                    case "warning" -> Level.WARNING;
                    case "info" -> Level.INFO;
                    case "config" -> Level.CONFIG;
                    case "fine" -> Level.FINE;
                    default -> null;
                };

                if (currentLogLevel != null)
                    Logging.setLogLevel(currentLogLevel);
            }
        }

        if (currentLogLevel != null && currentLogLevel != Level.INFO)
            System.out.println("Using Logging Level: " + currentLogLevel);
    }
}
