package org.mdt.tcpmodem.config;

import org.mdt.tcpmodem.hayes.ActionList;
import org.mdt.tcpmodem.hayes.DirectionList;
import org.mdt.tcpmodem.hayes.EventActionInfo;
import org.mdt.tcpmodem.misc.Utility;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ConfigLoader
{
    private static final Logger m_log = Logger.getLogger(ConfigLoader.class.getName());

    /**
     * Attempt to parse and load the config.xml.
     * @param configFile Path object to the config.xml
     * @return true on success
     */
    public static boolean loadConfig(Path configFile)
    {
        if (configFile==null) return false;
        if (!Files.exists(configFile))
        {
            m_log.severe("Could not find config.xml [" + configFile + "]");
            return false;
        }
        if (!Files.isRegularFile(configFile))
        {
            m_log.severe(configFile + " is not a regular file.");
            return false;
        }

        XMLInputFactory xmlInputFactory = Utility.getStaxReaderFactory();
        try(InputStream inputStream = Files.newInputStream(configFile))
        {
            ModemPool modemPool = null;
            Modem modem = null;

            XMLStreamReader xmlReader = xmlInputFactory.createXMLStreamReader(inputStream);
            while(xmlReader.hasNext())
            {
                int entryType = xmlReader.next();
                if (entryType == XMLStreamReader.START_ELEMENT)
                {
                    String xmlElement = xmlReader.getLocalName();

                    if (xmlElement.equals("ModemPool"))
                    {
                        modemPool = new ModemPool();
                        Config.getModemPools().add(modemPool);
                    }

                    if (xmlElement.equals("Modem"))
                    {
                        modem = new Modem();
                        modem.setName(Utility.getString(xmlReader.getAttributeValue(null, "name")));
                        modem.setType(Utility.getString(xmlReader.getAttributeValue(null, "type")));
                        modem.setDevice(Utility.getString(xmlReader.getAttributeValue(null, "port")));
                        modem.setSpeed(Utility.convertToInteger(xmlReader.getAttributeValue(null, "speed")));
                        modem.setPlayModemSounds(Utility.getString(xmlReader.getAttributeValue(null, "playModemSounds")).equals("YES"));

                        if (modemPool!=null)
                            modemPool.getModems().add(modem);
                    }

                    if (xmlElement.equals("PhoneBookEntry"))
                    {
                        String number = Utility.getString(xmlReader.getAttributeValue(null, "number"));
                        String value = Utility.getString(xmlReader.getAttributeValue(null, "value"));
                        if (modem!=null && number.length()>0 && value.length()>0)
                            modem.getPhoneBookEntries().add(new PhoneBookEntry(number, value));
                    }

                    if (xmlElement.equals("EventAction"))
                    {
                        String type = Utility.getString(xmlReader.getAttributeValue(null, "type"));
                        String action = Utility.getString(xmlReader.getAttributeValue(null, "action"));
                        String direction = Utility.getString(xmlReader.getAttributeValue(null, "direction"));
                        String content = Utility.getString(xmlReader.getAttributeValue(null, "content"));

                        if (modem!=null)
                            modem.getEventActionInfos().add(new EventActionInfo(type,
                                    ActionList.valueOf(action),
                                    DirectionList.valueOf(direction),
                                    content));
                    }

                    if (xmlElement.equals("Initialization"))
                    {
                        String initLine = Utility.getString(xmlReader.getElementText()).trim();
                        if (modem!=null && initLine.length()>0)
                            modem.getInitializations().add(initLine);
                    }
                }
            }
            xmlReader.close();
        }
        catch(XMLStreamException | IOException e)
        {
            m_log.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }

        return true;
    }
}
