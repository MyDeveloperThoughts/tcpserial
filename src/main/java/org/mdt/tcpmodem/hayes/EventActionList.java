package org.mdt.tcpmodem.hayes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventActionList
{
    public static final int DIR_BOTH = 2;
    public static final int DIR_REMOTE = 1;
    public static final int DIR_LOCAL = 0;
    private final EventActionList m_parent;
    private final Map<Integer, List<EventAction>> m_actions = new HashMap<>();

    public EventActionList()
    {
        m_parent = null;
    }

    public EventActionList(EventActionList store)
    {
        m_parent = store;
    }

    public void add(EventAction audioAction)
    {
        Integer action = audioAction.getAction();

        if (!m_actions.containsKey(action))
            m_actions.put(action, new ArrayList<>());

        List<EventAction> eventActions = m_actions.get(action);
        eventActions.add(audioAction);
    }

    public void execute(ModemEvent event, int direction)
    {
        Integer i = event.getType();

        if (m_parent != null)
            m_parent.execute(event, direction);

        if (m_actions.containsKey(i))
        {
            List<EventAction> eventActions = m_actions.get(i);
            for (EventAction eventAction : eventActions)
                if (direction == DIR_BOTH || direction == eventAction.getDirection())
                    eventAction.execute(event);
        }
    }
}
