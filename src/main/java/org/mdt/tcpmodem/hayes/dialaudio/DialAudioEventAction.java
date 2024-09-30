package org.mdt.tcpmodem.hayes.dialaudio;

import org.mdt.tcpmodem.hayes.AudioEventAction;
import org.mdt.tcpmodem.hayes.ModemCore;
import org.mdt.tcpmodem.hayes.ModemEvent;
import org.mdt.tcpmodem.misc.Utility;

import java.nio.file.Files;
import java.nio.file.Path;

public class DialAudioEventAction extends AudioEventAction
{
    public DialAudioEventAction(int direction, int action, String data, int iterations, boolean asynchronous)
    {
        super(direction, action, data, iterations, asynchronous);
    }

    private void delay(int delay)
    {
        try
        {
            Thread.sleep(delay);
        }
        catch (InterruptedException e)
        {
            // dont care
        }
    }

    void play(String numberButtonFilename, int delay)
    {
        if (numberButtonFilename != null)
        {
            try
            {
                Path audioFile = Utility.getResourceAsPath(numberButtonFilename);
                if (audioFile!=null)
                    AudioEventAction.play(Files.newInputStream(audioFile), 1, true);
            }
            catch (Exception e)
            {
                // dont care
            }
            delay(delay);
        }
    }

    public void execute(ModemEvent event)
    {
        int delay;
        if (event.getSource() instanceof ModemCore core)
        {
            if (!core.getConfig().isPlayModemSounds()) return;

            String stringData = core.getLastNumber().getData();
            delay = core.getConfig().getRegister(11);
            int stringDataLength = stringData.length();
            for (int i = 0; i < stringDataLength; i++)
            {
                switch (Character.toLowerCase(stringData.charAt(i)))
                {
                    case '0':
                    case 'q':
                    case 'x':
                        play("zero.au", delay);
                        break;
                    case '1':
                        play("one.au", delay);
                        break;
                    case '2':
                    case 'a':
                    case 'b':
                    case 'c':
                        play("two.au", delay);
                        break;
                    case '3':
                    case 'd':
                    case 'e':
                    case 'f':
                        play("three.au", delay);
                        break;
                    case '4':
                    case 'g':
                    case 'h':
                    case 'i':
                        play("four.au", delay);
                        break;
                    case '5':
                    case 'j':
                    case 'k':
                    case 'l':
                        play("five.au", delay);
                        break;
                    case '6':
                    case 'm':
                    case 'n':
                    case 'o':
                        play("six.au", delay);
                        break;
                    case '7':
                    case 'p':
                    case 'r':
                    case 's':
                        play("seven.au", delay);
                        break;
                    case '8':
                    case 't':
                    case 'u':
                    case 'v':
                        play("eight.au", delay);
                        break;
                    case '9':
                    case 'w':
                    case 'y':
                    case 'z':
                        play("nine.au", delay);
                        break;
                    case '*':
                        play("star.au", delay);
                        break;
                    case '#':
                        play("hash,au", delay);
                        break;
                    case ',':
                        delay(core.getConfig().getRegister(8) * 1000);
                        break;
                }
            }
        }
    }
}

