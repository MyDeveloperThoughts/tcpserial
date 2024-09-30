package org.mdt.tcpmodem.hayes;

public class ExtModemEventListener implements ModemEventListener
{
    EventActionList m_actionList;

    public ExtModemEventListener(EventActionList actionList)
    {
        super();
        m_actionList = actionList;
    }

    public void handleEvent(ModemEvent event)
    {
        m_actionList.execute(event, EventActionList.DIR_BOTH);
    }
}
