package org.mdt.tcpmodem.hayes;

import java.util.HashMap;
import java.util.Map;

public class ActionList implements java.io.Serializable
{
    public static final ActionList VALUE_ring = new ActionList("ring");
    public static final ActionList VALUE_cmd_mode = new ActionList("cmd_mode");
    public static final ActionList VALUE_data_mode = new ActionList("data_mode");
    public static final ActionList VALUE_off_hook = new ActionList("off_hook");
    public static final ActionList VALUE_on_hook = new ActionList("on_hook");
    public static final ActionList VALUE_pre_answer = new ActionList("pre_answer");
    public static final ActionList VALUE_answer = new ActionList("answer");
    public static final ActionList VALUE_pre_connect = new ActionList("pre_connect");
    public static final ActionList VALUE_connect = new ActionList("connect");
    public static final ActionList VALUE_dial = new ActionList("dial");
    public static final ActionList VALUE_busy = new ActionList("busy");
    public static final ActionList VALUE_no_answer = new ActionList("no_answer");
    public static final ActionList VALUE_hangup = new ActionList("hangup");
    public static final ActionList VALUE_inactivity = new ActionList("inactivity");

    private static final Map<String, ActionList> m_memberTable = init();

    private final String _sValue;

    public String toString()
    {
        return _sValue;
    }

    private ActionList(String val)
    {
        _sValue = val;
    }

    private static Map<String, ActionList> init()
    {
        Map<String, ActionList> members = new HashMap<>();
        members.put("ring", VALUE_ring);
        members.put("cmd_mode", VALUE_cmd_mode);
        members.put("data_mode", VALUE_data_mode);
        members.put("off_hook", VALUE_off_hook);
        members.put("on_hook", VALUE_on_hook);
        members.put("pre_answer", VALUE_pre_answer);
        members.put("answer", VALUE_answer);
        members.put("pre_connect", VALUE_pre_connect);
        members.put("connect", VALUE_connect);
        members.put("dial", VALUE_dial);
        members.put("busy", VALUE_busy);
        members.put("no_answer", VALUE_no_answer);
        members.put("hangup", VALUE_hangup);
        members.put("inactivity", VALUE_inactivity);
        return members;
    }

    public static ActionList valueOf(String type)
    {
        try
        {
            // quick fix for leading zero supression.  Fix later jlb
            type = String.valueOf(Integer.parseInt(type));
        }
        catch (Exception e)
        {
            // dont care
        }
        return m_memberTable.get(type);
    }

}
