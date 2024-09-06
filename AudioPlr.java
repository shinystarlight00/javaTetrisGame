import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioPlr{
    private Clip audioClip; //audio clip attribute
    private boolean musicOn;

    //constructor
    public AudioPlr(String path,boolean shouldLoop){
        setupAudio(path,shouldLoop); //create audio
    }
    private void setupAudio(String filePath,boolean shouldLoop) {
        try {
            // Open an audio input stream from a file
            File soundFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            AudioFormat format = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);

            // Get a clip resource
            audioClip = (Clip) AudioSystem.getLine(info);
            audioClip.open(audioStream);
            if (shouldLoop){
                audioClip.loop(Clip.LOOP_CONTINUOUSLY);
            }

            // Set volume (gain)
            FloatControl volumeControl = (FloatControl) audioClip.getControl(FloatControl.Type.MASTER_GAIN);
            volumeControl.setValue(-45.0f); // Reduce volume by 10 decibels

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    //Method for playing an audio clip
    public void playAudio() {
        musicOn=true;
        audioClip.start();
    }

    //method for stopping an audio clip
    public void endAudio() {
        musicOn=false;
        audioClip.stop();
    }

    public boolean isMusicOn(){
        return musicOn;
    }
}