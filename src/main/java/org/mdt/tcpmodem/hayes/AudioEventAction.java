package org.mdt.tcpmodem.hayes;

import org.mdt.tcpmodem.misc.Utility;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AudioEventAction extends AbstractEventAction
{
    private static final Logger m_log = Logger.getLogger(AudioEventAction.class.getName());

    public AudioEventAction(int direction, int action, String data, int iterations, boolean asynchronous)
    {
        super(direction, action, data, iterations, asynchronous);
    }

    public void execute(ModemEvent event)
    {
        String simpleFilename;      // Example: audio/dial_tone.wav

        if (event.getSource() instanceof ModemCore modemCore)
        {
            if (!modemCore.getConfig().isPlayModemSounds()) return;

            simpleFilename = replaceVars(modemCore, getContent());
        }
        else
            simpleFilename = getContent();
        try
        {
            Path fileNamePath = Utility.getResourceAsPath(simpleFilename);
            if (fileNamePath != null)
            {
                m_log.info("Playing audio clip: " + simpleFilename);
                play(new FileInputStream(fileNamePath.toString()), getIterations(), !isAsynchronous());
            }
        }
        catch (Exception e)
        {
            m_log.log(Level.SEVERE, "Error encountered playing audio clip", e);
        }
    }

    protected static void play(InputStream is, int iterations, boolean synchronous) throws UnsupportedAudioFileException, IOException, LineUnavailableException
    {
        final Object mutex = new Object();
        AudioInputStream stream;

        stream = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
        // At present, ALAW and ULAW encodings must be converted
        // to PCM_SIGNED before it can be played
        AudioFormat format = stream.getFormat();
        if (!format.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED) && !format.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED))
        {
            format = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    format.getSampleRate(),
                    format.getSampleSizeInBits() * 2,
                    format.getChannels(),
                    format.getFrameSize() * 2,
                    format.getFrameRate(),
                    true);        // big endian
            stream = AudioSystem.getAudioInputStream(format, stream);
        }

        // Create the clip
        DataLine.Info info = new DataLine.Info(
                Clip.class, stream.getFormat(), ((int) stream.getFrameLength() * format.getFrameSize()));
        Clip clip = (Clip) AudioSystem.getLine(info);
        // This method does not return until the audio file is completely loaded
        clip.open(stream);
        clip.loop(iterations - 1);
        if (synchronous)
        {
            //	      Add a listener for line events
            clip.addLineListener(new LineListener()
            {
                public void update(LineEvent evt)
                {
                    if (evt.getType() == LineEvent.Type.STOP)
                    {
                        synchronized (mutex)
                        {
                            mutex.notify();
                        }
                    }
                }
            });
            try
            {
                synchronized (mutex)
                {
                    mutex.wait();
                }
            }
            catch (InterruptedException e)
            {
                // dont care
            }
        }
    }
}



