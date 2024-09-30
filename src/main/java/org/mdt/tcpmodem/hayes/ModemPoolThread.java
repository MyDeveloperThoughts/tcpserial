package org.mdt.tcpmodem.hayes;

import org.mdt.tcpmodem.config.Modem;
import org.mdt.tcpmodem.config.ModemPool;
import org.mdt.tcpmodem.config.PhoneBookLogic;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ModemPoolThread extends Thread
{
    private static final Logger m_log = Logger.getLogger(ModemPoolThread.class.getName());
    private int m_socketPort = 0;
    List<ExtModemCore> m_extModemCores = new ArrayList<>();
    EventActionList m_defaultActionList = new EventActionList();

    public int getPort()
    {
        return m_socketPort;
    }

    public void setPort(int port)
    {
        m_socketPort = port;
    }

    public void restart()
    {
    }

    public ModemPoolThread(ModemPool pool) throws Exception
    {
        EventActionList actionList;
        ModemConfig cfg;
        StringBuilder sbInit = new StringBuilder();
        LinePortFactory factory = new ExtLinePortFactory();

        if (pool.getLine() != null && pool.getLine().getPort() != null)
            m_socketPort = pool.getLine().getPort();

        for (Modem modem : pool.getModems())
        {
            DCEPort port = getPort(modem);
            if (port != null)
            {
                cfg = new ModemConfig();
                cfg.setPlayModemSounds(modem.isPlayModemSounds());
                cfg.setDCESpeed(modem.getSpeed());
                actionList = new EventActionList(m_defaultActionList);
                addActions(actionList, modem);
                Properties phoneBook = new Properties();
                PhoneBookLogic.buildPhoneBook(modem.getPhoneBookEntries(), phoneBook);
                ExtModemCore extModemCore = new ExtModemCore(port, cfg, factory, phoneBook);
                extModemCore.setDCDInverted(modem.getInvertDCD() != null ? modem.getInvertDCD() : false);
                extModemCore.setOutput(false);
                sbInit.setLength(0);
                sbInit.append("at");
                for (String initText : modem.getInitializations())
                    sbInit.append(initText);
                sbInit.append((char) cfg.getRegister(3));

                m_log.info("[" + modem.getName() + "] Type: " + modem.getType() + "  Port: " + modem.getDevice() + "  Speed: " + modem.getSpeed() + "bps.");
                try
                {
                    m_log.info("[" + modem.getName() + "] " + sbInit);
                    extModemCore.parseData(sbInit.toString().getBytes(), sbInit.length());
                }
                catch (IOException e)
                {
                    m_log.severe("Could not set modem initialization");
                    throw e;
                }

                // TODO need to fix the cast below...
                if (modem.getCaptiveModem() != null)
                    extModemCore.setInternalLine((LinePort) getPort(modem.getCaptiveModem()));
                extModemCore.setOutput(true);
                extModemCore.addEventListener(new ExtModemEventListener(actionList));
                m_extModemCores.add(extModemCore);
                m_log.info("[" + modem.getName() + "] Waiting for IP232 connections on port " + modem.getDevice());
            }
        }
        setDaemon(true);
        start();

    }

    public DCEPort getPort(Modem modem) throws Exception
    {
        String type = modem.getType().toLowerCase();
        int speed = modem.getSpeed();
        DCEPort port = null;

        if (type.equals("remote232"))
        {
            try
            {
                HostAddress hostAddress = new HostAddress(modem.getDevice());
                port = new NVT232Port(hostAddress.getHost(), hostAddress.getPort(), speed);
            }
            catch (NumberFormatException e)
            {
                m_log.severe(modem.getDevice() + " is invalid.");
                throw e;
            }
        }

        if (type.equals("ip232"))
        {
            try
            {
                port = new IP232Port(modem.getName(), Integer.parseInt(modem.getDevice()), speed);
            }
            catch (NumberFormatException e)
            {
                m_log.severe(modem.getDevice() + " is not a valid IP232Port port number.");
                throw e;
            }
            catch (PortException e)
            {
                m_log.log(Level.SEVERE, modem.getDevice() + " returned error during initialization.", e);
                throw e;
            }
        }

        return port;
    }

    public void run()
    {
        // listen for incoming connections.
        try(ServerSocket listenSock = new ServerSocket(m_socketPort))
        {
            while (true)
            {
                TCPPort ipCall = new TCPPort(listenSock.accept());
                ModemCore modem = null;

                // try to find a listening modem.
                for (int i = 0, size = m_extModemCores.size(); modem == null && i < size; i++)
                {
                    modem = m_extModemCores.get(i);
                    if (!(modem.isWaitingForCall() && modem.acceptCall(ipCall))) modem = null;
                }

                // try to find an inactive modem.
                for (int i = 0, size = m_extModemCores.size(); modem == null && i < size; i++)
                {
                    modem = m_extModemCores.get(i);
                    if (!(!modem.isOffHook() && modem.acceptCall(ipCall))) modem = null;
                }

                if (modem == null)
                {
                    OutputStream os = ipCall.getOutputStream();

                    m_defaultActionList.execute(new ModemEvent(this, ModemEvent.RESPONSE_BUSY), EventActionList.DIR_REMOTE);
                    // TODO fix this
                    //if(msg==null) {
                    //	os.write("BUSY\r\n".getBytes());
                    //}
                    // close modem
                    ipCall.setDTR(false);
                }
            }
        }
        catch (Exception e)
        {
            m_log.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void addActions(EventActionList store, Modem modem)
    {
        EventActionInfo info;
        EventAction ea = null;
        int dir, action = 0, iter;
        boolean asynch;

        for (int j = 0, len = modem.getEventActionInfos().size(); j < len; j++)
        {
            info = modem.getEventActionInfos().get(j);
            if (info.getDirection() == DirectionList.VALUE_local)
            {
                dir = EventActionList.DIR_LOCAL;
            } else
            {
                dir = EventActionList.DIR_REMOTE;
            }
            if (info.getAction().equals(ActionList.VALUE_off_hook))
            {
                action = ModemEvent.OFF_HOOK;
            } else if (info.getAction().equals(ActionList.VALUE_on_hook))
            {
                action = ModemEvent.ON_HOOK;
            } else if (info.getAction().equals(ActionList.VALUE_pre_answer))
            {
                action = ModemEvent.PRE_ANSWER;
            } else if (info.getAction().equals(ActionList.VALUE_answer))
            {
                action = ModemEvent.ANSWER;
            } else if (info.getAction().equals(ActionList.VALUE_busy))
            {
                action = ModemEvent.RESPONSE_BUSY;
            } else if (info.getAction().equals(ActionList.VALUE_pre_connect))
            {
                action = ModemEvent.PRE_CONNECT;
            } else if (info.getAction().equals(ActionList.VALUE_connect))
            {
                action = ModemEvent.CONNECT;
            } else if (info.getAction().equals(ActionList.VALUE_inactivity))
            {
                // TODO add this code
                //action=ModemEvent.INACTIVITY;
            } else if (info.getAction().equals(ActionList.VALUE_no_answer))
            {
                action = ModemEvent.RESPONSE_NO_ANSWER;
            } else if (info.getAction().equals(ActionList.VALUE_dial))
            {
                action = ModemEvent.DIAL;
            } else if (info.getAction().equals(ActionList.VALUE_ring))
            {
                action = ModemEvent.RING;
            } else if (info.getAction().equals(ActionList.VALUE_hangup))
            {
                action = ModemEvent.HANGUP;
            }
            if (info.getIterations() != null) iter = info.getIterations().intValue();
            else iter = 1;
            if (info.getAsynchronous() != null) asynch = info.getAsynchronous().booleanValue();
            else asynch = false;
            if (info.getContent() != null)
            {
                String actionType = info.getType().toLowerCase();
                if (actionType.equals("file"))
                {
                    ea = new FileEventAction(dir, action, info.getContent(), iter, asynch);
                } else if (actionType.equals("java"))
                {
                    ea = AbstractEventAction.InstantiateEventAction(dir, action, info.getContent(), iter, asynch);
                } else if (actionType.equals("exec"))
                {
                    ea = new ExecEventAction(dir, action, info.getContent(), iter, asynch);
                } else if (actionType.equals("url"))
                {
                    ea = new URLEventAction(dir, action, info.getContent(), iter, asynch);
                } else if (actionType.equals("audio"))
                {
                    ea = new AudioEventAction(dir, action, info.getContent(), iter, asynch);
                }
            }
            if (ea != null) store.add(ea);
            ea = null;
        }
    }
}
