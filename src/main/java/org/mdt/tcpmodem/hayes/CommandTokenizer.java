package org.mdt.tcpmodem.hayes;

import org.mdt.tcpmodem.exceptions.CommandException;
import org.mdt.tcpmodem.exceptions.CommandNotFoundException;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandTokenizer
{
    private static final Logger m_log = Logger.getLogger(CommandTokenizer.class.getName());
    private static Map<Character, Map<Character, Class>> m_atCommandsByTypeMap = new HashMap<>();
    private int m_position;
    private int m_length;
    private byte[] m_lineBytes;

    static
    {
        try
        {
            addCommand(Command.TYPE_NORMAL, 'A', "org.mdt.tcpmodem.hayes.commands.AnswerCommand");
            addCommand(Command.TYPE_NORMAL, 'B', "org.mdt.tcpmodem.hayes.commands.ProtocolCommand");
            addCommand(Command.TYPE_NORMAL, 'D', "org.mdt.tcpmodem.hayes.commands.DialCommand");
            addCommand(Command.TYPE_NORMAL, 'E', "org.mdt.tcpmodem.hayes.commands.EchoCommand");
            addCommand(Command.TYPE_NORMAL, 'H', "org.mdt.tcpmodem.hayes.commands.HangupCommand");
            addCommand(Command.TYPE_NORMAL, 'L', "org.mdt.tcpmodem.hayes.commands.LoudnessCommand");
            addCommand(Command.TYPE_NORMAL, 'M', "org.mdt.tcpmodem.hayes.commands.SpeakerCommand");
            addCommand(Command.TYPE_NORMAL, 'V', "org.mdt.tcpmodem.hayes.commands.VerboseCommand");
            addCommand(Command.TYPE_NORMAL, 'P', "org.mdt.tcpmodem.hayes.commands.PulseCommand");
            addCommand(Command.TYPE_NORMAL, 'Q', "org.mdt.tcpmodem.hayes.commands.QuietCommand");
            addCommand(Command.TYPE_NORMAL, 'T', "org.mdt.tcpmodem.hayes.commands.ToneCommand");
            addCommand(Command.TYPE_NORMAL, 'S', "org.mdt.tcpmodem.hayes.commands.Register");
            addCommand(Command.TYPE_NORMAL, 'X', "org.mdt.tcpmodem.hayes.commands.ResponseLevelCommand");
            addCommand(Command.TYPE_NORMAL, 'Z', "org.mdt.tcpmodem.hayes.commands.ResetCommand");
            addCommand(Command.TYPE_NORMAL, '=', "org.mdt.tcpmodem.hayes.commands.DefaultRegister");
            addCommand(Command.TYPE_NORMAL, '?', "org.mdt.tcpmodem.hayes.commands.DefaultRegister");

            addCommand(Command.TYPE_EXTENDED, 'C', "org.mdt.tcpmodem.hayes.commands.ForceDCDCommand");
            addCommand(Command.TYPE_EXTENDED, 'D', "org.mdt.tcpmodem.hayes.commands.DTRCommand");
            addCommand(Command.TYPE_EXTENDED, 'K', "org.mdt.tcpmodem.hayes.commands.FlowControlCommand");
        }
        catch (ClassNotFoundException e)
        {
            m_log.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException("Cannot create CommandTokenizer");
        }
    }

    public CommandTokenizer(byte[] lineBytes, int len)
    {
        m_position = 0;
        m_length = len;
        m_lineBytes = lineBytes;
    }

    public void reset()
    {
        m_position = 0;
    }

    public Command next() throws CommandException
    {
        char type = Command.TYPE_NORMAL;
        char cmd;

        while (m_position < m_length)
        {
            cmd = Character.toUpperCase((char) m_lineBytes[m_position]);
            m_position++;
            switch (cmd)
            {
                case ' ':
                    // skip over spaces.
                    break;
                case 0:
                    // assume that null terminates
                    return null;
                case '%':
                    type = Command.TYPE_PROP_PERCENT;
                    break;
                case '\\':
                    type = Command.TYPE_PROP_BACKSLASH;
                    break;
                case ':':
                    type = Command.TYPE_PROP_COLON;
                    break;
                case '-':
                    type = Command.TYPE_PROP_MINUS;
                    break;
                case '&':
                    type = Command.TYPE_EXTENDED;
                    break;
                default:
                    // skip over spaces.
                    while (m_position < m_length && m_lineBytes[m_position] == ' ')
                        m_position++;
                    Command command = getCommand(type, cmd);
                    m_position = command.parse(m_lineBytes, m_position, m_length);
                    return command;
            }
        }
        return null;
    }

    private Command getCommand(char type, char cmd) throws CommandException
    {
        Map<Character, Class> commandToClassMap = m_atCommandsByTypeMap.get(type);
        if (commandToClassMap != null)
        {
            Class commandClass = commandToClassMap.get(cmd);
            if (commandClass != null)
            {
                Command inst;
                try
                {
                    inst = (Command) commandClass.getDeclaredConstructor().newInstance();
                    inst.init(type, cmd);
                    return inst;
                }
                catch (InstantiationException e)
                {
                    m_log.log(Level.SEVERE, e.getMessage(), e);
                    throw new CommandException("Cannot create command " + cmd, e);
                }
                catch (IllegalAccessException e)
                {
                    m_log.log(Level.SEVERE, e.getMessage(), e);
                    throw new CommandException("Cannot access command " + cmd, e);
                }
                catch (NoSuchMethodException e)
                {
                    m_log.log(Level.SEVERE, e.getMessage(), e);
                    throw new CommandException("No Such Method " + cmd, e);
                }
                catch (InvocationTargetException e)
                {
                    m_log.log(Level.SEVERE, e.getMessage(), e);
                    throw new CommandException("Invocation Target Exception " + cmd, e);
                }
            }
        }
        throw new CommandNotFoundException(cmd);
    }

    private static void addCommand(char type, char cmd, String command) throws ClassNotFoundException
    {
        Character key = type;
        if (!m_atCommandsByTypeMap.containsKey(key))
            m_atCommandsByTypeMap.put(key, new HashMap<>());

        Map<Character, Class> commandToClassMap =m_atCommandsByTypeMap.get(key);
        Class commandClass = Class.forName(command);

        commandToClassMap.put(cmd, commandClass);
    }
}
