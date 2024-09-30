package org.mdt.tcpmodem.hayes;

public class SerialEvent extends java.util.EventObject
{
    private int m_type;
    private boolean m_old;
    private boolean m_new;

    public SerialEvent(Object object, int type, boolean oldValue, boolean newValue)
    {
        super(object);
        m_new = newValue;
        m_old = oldValue;
        m_type = type;
    }

    public int getEventType()
    {
        return m_type;
    }

    public boolean getNewValue()
    {
        return m_new;
    }

    public boolean getOldValue()
    {
        return m_old;
    }


}
