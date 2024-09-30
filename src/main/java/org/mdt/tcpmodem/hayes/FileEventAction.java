package org.mdt.tcpmodem.hayes;

import org.mdt.tcpmodem.misc.Utility;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

public class FileEventAction extends AbstractEventAction
{
    private static final Logger m_log = Logger.getLogger(FileEventAction.class.getName());

    public FileEventAction(int direction, int action, String data, int iterations, boolean asynchronous)
    {
        super(direction, action, data, iterations, asynchronous);
    }

    public void execute(ModemEvent event)
    {
        OutputStream outputStream;
        ModemCore modemCore;
        String data;


        if (event.getSource() instanceof ModemCore)
        {
            modemCore = (ModemCore) event.getSource();
            data = replaceVars(modemCore, getContent());
            try
            {
                outputStream = getOutputStream(modemCore);
                if (outputStream != null)
                {
                    if (getDirection() == EventActionList.DIR_LOCAL)
                    {
                        m_log.info("Sending inbound file: " + data);
                    }
                    else
                    {
                        m_log.info("Sending outbound file: " + data);
                    }
                    Utility.copyData(new FileInputStream(data), outputStream);
                }
            } catch (IOException e)
            {
                m_log.severe("Encountered exception while trying to send file");
            }
        }
        else
        {
            // must be a ModemPool.
        }
    }
}
