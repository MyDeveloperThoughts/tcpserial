package org.mdt.tcpmodem.hayes;

import java.util.HashMap;
import java.util.Map;

public class DirectionList implements java.io.Serializable
{
    public static final DirectionList VALUE_local = new DirectionList("local");
    public static final DirectionList VALUE_remote = new DirectionList("remote");

    private static final Map<String, DirectionList> m_typeToDirectionListMap = init();

    private final String m_value;

    private DirectionList(String val)
    {
        m_value = val;
    }

    private static Map<String, DirectionList> init()
    {
        Map<String, DirectionList> members = new HashMap<>();
        members.put("local", VALUE_local);
        members.put("remote", VALUE_remote);
        return members;
    }

    public String toString()
    {
        return m_value;
    }

    public static DirectionList valueOf(String type)
    {
        try
        {
            // quick fix for leading zero suppression.  Fix later jlb
            type = String.valueOf(Integer.parseInt(type));
        }
        catch (Exception e)
        {
            // dont care
        }
        return m_typeToDirectionListMap.get(type);
    }

}
