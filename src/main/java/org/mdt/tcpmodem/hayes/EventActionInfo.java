package org.mdt.tcpmodem.hayes;

public class EventActionInfo implements java.io.Serializable
{
    private String m_type = null;
    private ActionList m_action = null;
    private DirectionList direction = null;
    private String m_content = null;
    private Integer m_iterations = null;
    private Boolean m_oAsynchronous = null;

    public EventActionInfo()
    {
    }

    public EventActionInfo(String type, ActionList action, DirectionList direction, String content)
    {
        this.m_type = type;
        this.m_action = action;
        this.direction = direction;
        this.m_content = content;
    }

    public String getType()
    {
        return m_type;
    }

    public void setType(String o)
    {
        m_type = o;
    }

    public ActionList getAction()
    {
        return m_action;
    }

    public void setAction(ActionList o)
    {
        m_action = o;
    }

    public DirectionList getDirection()
    {
        return direction;
    }

    public void setDirection(DirectionList o)
    {
        direction = o;
    }

    public String getContent()
    {
        return m_content;
    }

    public void setContent(String o)
    {
        m_content = o;
    }

    public Integer getIterations()
    {
        return m_iterations;
    }

    public void setIterations(Integer o)
    {
        m_iterations = o;
    }

    public Boolean getAsynchronous()
    {
        return m_oAsynchronous;
    }

    public void setAsynchronous(Boolean o)
    {
        m_oAsynchronous = o;
    }

}
