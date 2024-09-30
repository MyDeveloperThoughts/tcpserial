package org.mdt.tcpmodem.hayes;

import org.mdt.tcpmodem.misc.Utility;

import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class URLEventAction extends AbstractEventAction
{
    private static final Logger m_log = Logger.getLogger(URLEventAction.class.getName());

    public URLEventAction(int direction, int action, String data, int iterations, boolean asynchronous)
    {
        super(direction, action, data, iterations, asynchronous);
    }

    public void execute(ModemEvent event)
    {
        ModemCore core;
        String url;
        OutputStream os;

        try
        {
            if (event.getSource() instanceof ModemCore)
            {
                core = (ModemCore) event.getSource();
                os = getOutputStream(core);
                url = replaceVars(core, getContent());
            }
            else
            {
                url = getContent();
                os = null;
            }
            if (os != null)
            {
                if (getDirection() == EventActionList.DIR_LOCAL)
                {
                    m_log.info("Sending inbound url: " + url);
                }
                else
                {
                    m_log.info("Sending outbound url: " + url);
                }
                URL conn = new URI(url).toURL();
                URLConnection uc = conn.openConnection();
                Utility.copyData(uc.getInputStream(), os);
                uc.getInputStream().close();
            }
        }
        catch (Exception e)
        {
            m_log.log(Level.SEVERE, "Error while retrieving URL: " + getContent(), e);
        }
    }
}

