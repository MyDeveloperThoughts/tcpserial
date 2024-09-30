package org.mdt.tcpmodem.hayes;

import org.mdt.tcpmodem.hayes.commands.DialCommand;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractEventAction implements EventAction
{
    private static final Logger m_log = Logger.getLogger(AbstractEventAction.class.getName());
    private final int m_direction;
    private final int m_action;
    private final String m_content;
    private final int m_iterations;
    private final boolean m_bAsynchronous;


    public AbstractEventAction(int dir, int action, String data, int iterations, boolean b)
    {
        m_direction = dir;
        m_action = action;
        m_content = data;
        m_iterations = iterations;
        m_bAsynchronous = b;
    }

    public int getAction()
    {
        return m_action;
    }

    public int getDirection()
    {
        return m_direction;
    }

    public String getContent()
    {
        return m_content;
    }

    public int getIterations()
    {
        return m_iterations;
    }

    public boolean isAsynchronous()
    {
        return m_bAsynchronous;
    }

    protected String replaceVars(Map<String, Object> mapOfStringValues, String data)
    {
        Iterator<String> i = mapOfStringValues.keySet().iterator();
        String key;
        String val;
        int pos = 0;
        boolean bReplace = false;

        while (i.hasNext())
        {
            key = i.next();
            val = (String) mapOfStringValues.get(key);
            pos = 0;
            while (pos > -1)
            {
                pos = data.indexOf(key);
                if (pos > -1)
                {
                    bReplace = true;
                    data = data.substring(0, pos) + val + data.substring(pos + key.length());
                }
            }
        }
        if (bReplace)
            return replaceVars(mapOfStringValues, data);
        return data;
    }

    protected String replaceVars(ModemCore core, String data)
    {
        if (data.contains("${"))
        {
            Map<String, Object> m = new HashMap<>();
            m.put("${direction}", core.getConnDirection());
            m.put("${speed}", core.getSpeed());
            if (core.getLastNumber() != null)
            {
                m.put("${number}", core.getLastNumber().getData());
                m.put("${number.method}", core.getLastNumber().getModifier());
            }
            else
            {
                m.put("${number}", "");
                m.put("${number.method}", DialCommand.DIAL_DEFAULT);
            }
            return replaceVars(m, data);
        }
        return data;
    }

    protected OutputStream getOutputStream(ModemCore m) throws IOException
    {
        if (getDirection() == EventActionList.DIR_LOCAL)
        {
            return m.getDCEPort().getOutputStream();
        } else if (m.getLinePort() != null)
        {
            return m.getLinePort().getOutputStream();
        }
        return null;
    }

    public static EventAction InstantiateEventAction(int dir, int action, String content, int iter, boolean asynch)
    {
        try
        {
            Class c = ModemPoolThread.class.getClassLoader().loadClass(content);
            Class[] parmTypes = {int.class, int.class, String.class, int.class, boolean.class};
            Object[] parms = {dir, action, content, iter, asynch};
            Constructor cons = c.getConstructor(parmTypes);
            if (cons != null)
            {
                return (EventAction) cons.newInstance(parms);
            }
        } catch (Exception e)
        {
            m_log.log(Level.SEVERE, "Could not instantiate user-defined Java EventAction: " + content, e);
        }
        return null;
    }

}
