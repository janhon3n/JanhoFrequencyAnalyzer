import javax.sound.sampled.*;
import java.lang.annotation.Target;
import java.util.Scanner;

/**
 * Created by Miquel on 20.6.2016.
 */
public class AudioSampler {

    private TargetDataLine targetDataLine;
    private AudioFormat audioFormat;

    private float defaultSampleRate = 44100;
    private int defaultSampleSizeInBits = 8;
    private int defaultChannels = 1;
    private boolean defaultSigned = true;
    private boolean defaultBigEndian = false;


    public AudioSampler() {
        audioFormat = new AudioFormat(defaultSampleRate, defaultSampleSizeInBits, defaultChannels, defaultSigned, defaultBigEndian);
    }

    public AudioSampler(TargetDataLine targetDataLine) {
        this.targetDataLine = targetDataLine;
        audioFormat = new AudioFormat(defaultSampleRate, defaultSampleSizeInBits, defaultChannels, defaultSigned, defaultBigEndian);
    }

    public AudioSampler(TargetDataLine targetDataLine, AudioFormat audioFormat) {
        this.targetDataLine = targetDataLine;
        this.audioFormat = audioFormat;
    }

    public void setAudioFormat(AudioFormat audioFormat) {
        this.audioFormat = audioFormat;
    }

    public AudioFormat getAudioFormat() {
        return audioFormat;
    }

    public void openLine() throws LineUnavailableException {
        if (targetDataLine == null) {
            throw new LineUnavailableException("Line is not set yet");
        } else {
            targetDataLine.open(audioFormat);
            targetDataLine.start();
        }
    }

    public void closeLine() {
        targetDataLine.close();
    }

    public byte[] getSamplesByte(int amount) throws SamplerException, LineUnavailableException {
        if (targetDataLine == null) {
            throw new LineUnavailableException("Line is not set yet");
        } else {
            int dataAvailable = targetDataLine.available();
            if (dataAvailable < amount) {
                throw new SamplerException("Not enough data available in the dataline");
            }
            byte[] data = new byte[amount];
            targetDataLine.read(data, 0, amount);
            return data;
        }
    }

    public float[] getSamplesFloat(int amount) throws SamplerException, LineUnavailableException {
        byte[] data = getSamplesByte(amount);
        float[] floatData = new float[amount];
        System.out.println();
        switch (audioFormat.getSampleSizeInBits()) {
            case 8:
                for (int i = 0; i < data.length; i++) {
                    int byteValue = data[i];
                    floatData[i] = byteValue;
                }
                break;
            case 16:
                break;
            default:
                throw new SamplerException("Error with audio format");
        }
        return floatData;
    }
    public double[] getSamplesDouble(int amount)throws SamplerException, LineUnavailableException{
        byte[] data = getSamplesByte(amount);
        double[] doubleData = new double[amount];
        System.out.println();
        switch (audioFormat.getSampleSizeInBits()) {
            case 8:
                for (int i = 0; i < data.length; i++) {
                    int byteValue = data[i];
                    doubleData[i] = byteValue;
                }
                break;
            case 16:
                break;
            default:
                throw new SamplerException("Error with audio format");
        }
        return doubleData;
    }

    public boolean lineChosen(){
        if(targetDataLine == null){
            return false;
        } else {
            return true;
        }
    }

    public void chooseLineLUI(Scanner scanner) throws LineUnavailableException {
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        for (int i = 1; i <= mixerInfos.length; i++) {
            System.out.println(i + ". " + mixerInfos[i - 1].getName() + " - " + mixerInfos[i - 1].getDescription());
        }
        int mixerIndex = 0;
        while (true) {
            try {
                System.out.print("Choose a mixer: ");
                mixerIndex = scanner.nextInt();
                if (mixerIndex < 1 || mixerIndex > mixerInfos.length) {
                    throw new UserInputException("Invalid mixer index");
                } else {
                    break;
                }
            } catch (UserInputException uie) {
                uie.printStackTrace();
            }
        }
        Mixer mixer = AudioSystem.getMixer(mixerInfos[mixerIndex - 1]);

        Line.Info[] targetDataLineInfos = mixer.getTargetLineInfo();
        if (targetDataLineInfos.length == 0) {
            throw new LineUnavailableException("The mixer has no target data lines available");
        } else {
            this.targetDataLine = (TargetDataLine) mixer.getLine(targetDataLineInfos[0]);
        }
    }

    public void chooseDefaultLine() {
        //TODO
    }
}

