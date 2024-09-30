package org.mdt.tcpmodem.hayes;

import org.mdt.tcpmodem.hayes.commands.CommandResponse;
import org.mdt.tcpmodem.hayes.commands.DialCommand;
import org.mdt.tcpmodem.exceptions.CommandException;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

public class ExtModemCore extends ModemCore
{
    private LinePort m_captivePort;
    private final Properties m_phoneBookProperties;
    private static final Logger m_log = Logger.getLogger(ExtModemCore.class.getName());

    public ExtModemCore(DCEPort dcePort, ModemConfig cfg, LinePortFactory factory, Properties phoneBook)
    {
        super(dcePort, cfg, factory);
        if (phoneBook != null)
            m_phoneBookProperties = phoneBook;
        else
            m_phoneBookProperties = new Properties();
    }

    public CommandResponse dial(DialCommand cmd) throws PortException
    {
        DialCommand dialno;

        String alias = m_phoneBookProperties.getProperty(cmd.getData().trim().toLowerCase());
        if (alias != null)
        {
            // I found an alias, map in,
            // this is rather nasty, but I can;t think of another way right now.
            dialno = new DialCommand();
            String number = cmd.getModifier() + alias;
            try
            {
                dialno.parse(number.getBytes(), 0, number.length());
            }
            catch (CommandException e)
            {
                dialno = cmd;
            }
        }
        else
        {
            dialno = cmd;
        }
        return super.dial(dialno);
    }


    public CommandResponse hangup()
    {
        CommandResponse response = super.hangup();
        if (m_captivePort != null)
        {
            try
            {
                setLinePort(m_captivePort);
            }
            catch (PortException e)
            {
                m_log.severe("Could not set CaptivePort");
            }
        }
        return response;
    }


    public boolean acceptCall(LinePort call) throws PortException
    {
        if (getLinePort() == null || getLinePort() == m_captivePort)
        {
            // set port to null and go on.
            setLinePort(null);
            return super.acceptCall(call);
        }
        return false;
    }

    public void setInternalLine(LinePort port) throws PortException
    {
        // should set modem up here.
        m_captivePort = port;
        try
        {
            m_captivePort.getOutputStream().write("ATE0X0V0".getBytes());
            if (super.getLinePort() == null)
            {
                setLinePort(port);
            }
        }
        catch (IOException e)
        {
            throw new PortException("Could not configure captive modem", e);
        }
    }
}
