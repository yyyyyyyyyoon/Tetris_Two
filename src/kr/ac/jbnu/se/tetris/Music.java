package kr.ac.jbnu.se.tetris;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Music {
    private static Clip backgroundMusicClip;
    private static FloatControl gainControl;

    public static void playBackgroundMusic(String filename) {
        try {
            File soundFile = new File(filename);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);

            backgroundMusicClip = AudioSystem.getClip();
            backgroundMusicClip.open(audioInputStream);

            backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
            gainControl = (FloatControl) backgroundMusicClip.getControl(FloatControl.Type.MASTER_GAIN);

            backgroundMusicClip.start();
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void setVolume(float volume) {
        if (backgroundMusicClip != null) {
            float min = gainControl.getMinimum();
            float max = gainControl.getMaximum();
            float range = max - min;
            float gain = (range * volume) + min;
            gainControl.setValue(gain);
        }
    }
}
