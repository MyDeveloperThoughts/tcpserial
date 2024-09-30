package org.mdt.tcpmodem;

import java.util.logging.Logger;

public class Version
{
    private static final Logger m_log = Logger.getLogger(Main.class.getName());
    private final static String VERSION = "1.0";
    private final static String RELEASE_DATE = "Oct 1, 2024";

    public static void showStartupMessage()
    {
        m_log.info(String.format("TCPSerial v%s released on %s", VERSION, RELEASE_DATE));
        m_log.info("Java Version Information:");
        m_log.info(String.format("  %s %s",   System.getProperty("java.vm.name"), System.getProperty("java.vm.version")));
        m_log.info(String.format("  %s", System.getProperty("java.vm.vendor")));
        m_log.info("Press Control-C to stop");
    }
}
