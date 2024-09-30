package org.mdt.tcpmodem.hayes;

import java.util.logging.Level;
import java.util.logging.Logger;

public class EscapeTimer extends Thread
{
    private static final int STATE_NO_WAIT = 0;
    private static final int STATE_WAIT_FOR_PRE_DELAY = 1;
    private static final int STATE_WAIT_FOR_FIRST_CHAR = 2;
    private static final int STATE_WAIT_FOR_ESCAPE_CHARS = 3;
    private static final int STATE_WAIT_FOR_POST_DELAY = 4;
    private final ModemCore m_modemCore;
    private final Object m_mutex = new Object();

    private int m_numberOfEscapeCharacters = 0;

    private static final Logger m_log = Logger.getLogger(EscapeTimer.class.getName());

    public EscapeTimer(ModemCore core)
    {
        m_modemCore = core;
        setDaemon(true);
        start();
    }

    public void run()
    {
        int iState = STATE_NO_WAIT;
        int delay = 0;
        while (true)
        {
            try
            {
                if (iState == STATE_NO_WAIT || iState == STATE_WAIT_FOR_FIRST_CHAR)
                {
                    synchronized (this)
                    {
                        this.wait();
                    }
                }
                else
                {
                    synchronized (this)
                    {
                        this.wait(delay);
                    }
                    m_log.log(Level.FINE, "Timed out after " + delay + "ms.");
                    // we timed out., check things
                    synchronized (m_mutex)
                    {
                        if (m_modemCore.isCommandMode())
                        {
                            // reset
                        }
                        else
                        {
                            switch (iState)
                            {
                                case STATE_WAIT_FOR_PRE_DELAY:
                                    m_log.log(Level.FINE, "Initial delay found, watching for first escape char.");
                                    // we timed out while checking for quiet, move
                                    // to new state.
                                    m_numberOfEscapeCharacters = 0;
                                    iState = STATE_WAIT_FOR_FIRST_CHAR;
                                    break;
                                case STATE_WAIT_FOR_ESCAPE_CHARS:
                                    m_log.log(Level.FINE, "No escape chars found during delay, starting over.");
                                    // we timed out waiting for a char, go back.
                                    iState = STATE_WAIT_FOR_PRE_DELAY;
                                    delay = m_modemCore.getConfig().getRegister(12) * 20;
                                    break;
                                case STATE_WAIT_FOR_POST_DELAY:
                                    m_log.log(Level.FINE, "Post escape char delay found, switching to command mode.");
                                    // we timed out while waiting for post delay,
                                    // we are now in command mode.
                                    iState = STATE_NO_WAIT;
                                    m_modemCore.setCommandMode(true);
                                    // print OK.
                                    m_modemCore.sendResponse(ResponseMessage.OK, "Found escape sequence");
                                    break;
                            }
                        }
                    }
                }
            }
            catch (InterruptedException e)
            {
                m_log.log(Level.FINE, "Interrupted while waiting.");
                synchronized (m_mutex)
                {
                    // we were notified, check flags
                    if (m_modemCore.isCommandMode())
                    {
                        // reset
                        m_log.log(Level.FINE, "Resetting.");
                        iState = STATE_NO_WAIT;
                    }
                    else
                    {
                        switch (iState)
                        {
                            case STATE_NO_WAIT:
                                m_log.log(Level.FINE, "Checking for pre delay.");
                                // we should now start watching
                                iState = STATE_WAIT_FOR_PRE_DELAY;
                                delay = m_modemCore.getConfig().getRegister(12) * 20;
                                break;
                            case STATE_WAIT_FOR_PRE_DELAY:
                                m_log.log(Level.FINE, "Interrupted while checking for pre delay, starting over.");
                                // we were interrupted with a char while we were
                                // waiting, so start over;
                                break;
                            case STATE_WAIT_FOR_FIRST_CHAR:
                                // if we don't have 1+ escape char, start over.
                                if (m_numberOfEscapeCharacters > 0)
                                {
                                    m_log.log(Level.FINE, "Found first escape char, timing next ones.");
                                    iState = STATE_WAIT_FOR_ESCAPE_CHARS;
                                    delay = 1000;
                                }
                                else
                                {
                                    m_log.log(Level.FINE, "Found non escape char, starting over.");
                                    iState = STATE_WAIT_FOR_PRE_DELAY;
                                    delay = m_modemCore.getConfig().getRegister(12) * 20;
                                }
                                break;
                            case STATE_WAIT_FOR_ESCAPE_CHARS:
                                m_log.log(Level.FINE, "Interrupted while waiting for escape chars.");
                                // we were interrupted while waiting for a char,
                                // so check to see if we have enough.
                                if (m_numberOfEscapeCharacters == 3)
                                {
                                    m_log.log(Level.FINE, "3 escape chars found, checking for post delay.");
                                    delay = m_modemCore.getConfig().getRegister(12) * 20;
                                    iState = STATE_WAIT_FOR_POST_DELAY;
                                }
                                else
                                {
                                    // state stays the same.
                                }
                                break;
                            case STATE_WAIT_FOR_POST_DELAY:
                                m_log.log(Level.FINE, "Interrupted while waiting for post delay, starting over.");
                                // we were interrupted with a char while we were
                                // waiting for quiet, so start over.
                                iState = STATE_WAIT_FOR_PRE_DELAY;
                                m_numberOfEscapeCharacters = 0;
                                break;
                        }
                    }
                }
            }
        }
    }

    public void checkData(byte[] data, int start, int len)
    {
        byte ch = (byte) m_modemCore.getConfig().getRegister(2);
        for (int i = start; i < (start + len); i++)
        {
            if (data[i] == ch)
            {
                m_numberOfEscapeCharacters++;
                synchronized (this)
                {
                    this.interrupt();
                }
            }
            else
            {
                m_numberOfEscapeCharacters = 0;
                synchronized (this)
                {
                    this.interrupt();
                }
            }
        }
    }
}


