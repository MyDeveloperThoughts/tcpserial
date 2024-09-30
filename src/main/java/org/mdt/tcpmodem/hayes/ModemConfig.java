package org.mdt.tcpmodem.hayes;

import java.util.HashMap;
import java.util.Map;

public class ModemConfig
{
    private int m_DTRAction = 2;
    private int m_defRegister;
    private int m_flowControl;
    public static int DIAL_TYPE_PULSE = 'P';
    public static int DIAL_TYPE_TONE = 'T';
    public static int PROTOCOL_V22 = 0;
    public static int PROTOCOL_BELL_212A = 1;
    public static int FLOWCONTROL_NONE = 0;
    public static int FLOWCONTROL_RTSCTS_IN = 1;
    public static int FLOWCONTROL_RTSCTS_OUT = 2;
    public static int FLOWCONTROL_XONXOFF_IN = 4;
    public static int FLOWCONTROL_XONXOFF_OUT = 8;

    private int m_1200Protocol;
    private int m_dialType;
    private int m_speaker;
    private int m_loudness;
    private boolean m_quiet;
    private boolean m_forceDCD;
    private int m_DCESpeed;
    private int[] m_register;
    private boolean m_responses;
    private boolean m_verbose;
    private boolean m_echo;
    private int m_responseLevel;
    private Map<Integer, ModemConfig> m_configs = new HashMap<>();
    private boolean m_playModemSounds;

    public ModemConfig()
    {
        m_register = new int[100];
        m_responses = true;
        m_verbose = true;
        m_echo = true;
        m_forceDCD = false;
        m_dialType = DIAL_TYPE_TONE;
        m_responseLevel = 4;
        m_DCESpeed = 38400;
        m_1200Protocol = PROTOCOL_V22;
        m_flowControl = FLOWCONTROL_RTSCTS_IN | FLOWCONTROL_RTSCTS_OUT;
        m_DTRAction = 2;
        for (int i = 0; i < 100; i++)
            setRegister(i, 0);
        setRegister(2, 43);
        setRegister(3, 13);
        setRegister(4, 10);
        setRegister(5, 8);
        setRegister(6, 2);
        setRegister(7, 50);
        setRegister(8, 2);
        setRegister(9, 6);
        setRegister(10, 14);
        setRegister(11, 95);
        setRegister(12, 50);
    }

    public boolean isEcho()
    {
        return m_echo;
    }

    public void setConfig(int num, ModemConfig modemConfig)
    {
        m_configs.put(num, modemConfig);
    }

    public ModemConfig getConfig(int num)
    {
        ModemConfig cfg = m_configs.get(num);
        if (cfg == null)
            cfg = this;
        return cfg;
    }

    public void setRegister(int i, int j)
    {
        if (i < 100)
            m_register[i] = j;
    }

    public int getRegister(int i)
    {
        if (i < 100)
            return m_register[i];
        return 0;
    }

    public void setDCESpeed(int dceSpeed)
    {
        m_DCESpeed = dceSpeed;

    }

    public void setEcho(boolean b)
    {
        m_echo = b;

    }

    public void setDCDForced(boolean b)
    {
        m_forceDCD = b;
    }


    public void setResponseLevel(int level)
    {
        m_responseLevel = level;
    }

    public int getResponseLevel()
    {
        return m_responseLevel;
    }

    public boolean isVerbose()
    {
        return m_verbose;
    }

    public boolean isQuiet()
    {
        return m_quiet;
    }

    public void setLoudness(int i)
    {
        m_loudness = i;

    }

    public void setSpeakerControl(int i)
    {
        m_speaker = i;

    }

    public void setQuiet(boolean b)
    {
        m_quiet = b;
    }

    public void setVerbose(boolean b)
    {
        m_verbose = b;
    }

    public boolean isDCDForced()
    {
        return m_forceDCD;
    }

    public void set1200Protocol(int i)
    {
        m_1200Protocol = i;

    }

    public void setDialType(int i)
    {
        m_dialType = i;

    }

    public void setFlowControl(int i)
    {
        m_flowControl = i;

    }

    public void setDefaultRegister(int i)
    {
        m_defRegister = i;
    }

    public int getDefaultRegister()
    {
        return m_defRegister;
    }

    public int getDTRAction()
    {
        return m_DTRAction;
    }

    public void setDTRAction(int action)
    {
        m_DTRAction = action;
    }

    public boolean isPlayModemSounds()
    {
        return m_playModemSounds;
    }

    public void setPlayModemSounds(boolean playModemSounds)
    {
        m_playModemSounds = playModemSounds;
    }
}
