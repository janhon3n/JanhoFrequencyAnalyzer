import javax.sound.sampled.LineUnavailableException;
import java.util.Scanner;

/**
 * Created by Miquel on 20.6.2016.
 */
public class JanhoSignalAnalyzer {

    private FFT fft;
    private AudioSampler audioSampler;
    Grapher grapher;

    int bufferSize = 2048; //must be 2^n
    double[] buffer = new double[bufferSize];


    public JanhoSignalAnalyzer() {
        Scanner scanner = new Scanner(System.in);

        grapher = new Grapher();
        grapher.getFreqGraph().setMaxValue(bufferSize * 50);

        fft = new FFT();
        audioSampler = new AudioSampler();

        while(!audioSampler.lineChosen())
        try {
            audioSampler.chooseLineLUI(scanner);
        } catch (LineUnavailableException lue){
            lue.printStackTrace();
        }
    }

    public void begin() {
        try {
            audioSampler.openLine();
        } catch(LineUnavailableException lue){
            lue.printStackTrace();
        }

        while(true) {
            try {
                saveSamplesToBuffer(audioSampler.getSamplesDouble(512));
                double[] freqSamples = FFT.absFft(buffer);

                double[] freqSamplesCut = new double[freqSamples.length / 2];
                for(int i = 0; i < freqSamplesCut.length; i++){
                    freqSamplesCut[i] = freqSamples[i];
                }

                grapher.getTimeGraph().setData(buffer);
                grapher.getFreqGraph().setData(freqSamplesCut);

            } catch (SamplerException se) {
                //se.printStackTrace();
            } catch (LineUnavailableException lue) {
                lue.printStackTrace();
            }
        }
    }

    public void saveSamplesToBuffer(double[] samples) {
        if (samples.length >= buffer.length) {
            for (int i = 0; i < samples.length; i++) {
                buffer[i] = samples[i];
            }
        } else {
            //move data left to make room for the new data
            for (int i = 0; i < buffer.length - samples.length; i++) {
                buffer[i] = buffer[i + samples.length];
            }
            //add the new data to the end of the buffer
            for (int i = 0; i < samples.length; i++) {
                buffer[buffer.length - samples.length + i] = samples[i];
            }
        }
    }

    public static void main(String[] args) {
        JanhoSignalAnalyzer jsa = new JanhoSignalAnalyzer();
        jsa.begin();
    }
}