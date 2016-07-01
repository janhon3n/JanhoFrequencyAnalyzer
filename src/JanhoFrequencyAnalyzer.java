import javax.sound.sampled.LineUnavailableException;
import java.util.Scanner;

/**
 * Created by Miquel on 20.6.2016.
 */
public class JanhoFrequencyAnalyzer {

    private FFT fft;
    private AudioSampler audioSampler;
    Grapher grapher;

    int bufferSize = 16384;
    int samplesForFft = 8192; //must be 2^n
    int samplesCutAfterFft = 3072;
    int samplesToDrawFft = 128; //must fulfill: samplesToDrawFft * n = samplesForFft / 2, n = {1, 2, 3 ...}
    double[] buffer = new double[bufferSize];


    public JanhoFrequencyAnalyzer() {
        Scanner scanner = new Scanner(System.in);

        grapher = new Grapher();
        grapher.getFreqGraph().setMaxValue(samplesForFft * grapher.getFreqGraph().getMaxValue());

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
                double[] freqSamples = new double[samplesForFft];
                for(int i = 0; i < samplesForFft; i++){
                    freqSamples[i] = buffer[buffer.length - samplesForFft + i];
                }
                freqSamples = FFT.absFft(freqSamples);

                //split in half becouse the spectrum is twofold
                double[] freqSamplesCut = new double[freqSamples.length / 2 - samplesCutAfterFft];
                for(int i = 0; i < freqSamplesCut.length; i++){
                    freqSamplesCut[i] = freqSamples[i];
                }

                double[] freqSamplesToDraw = new double[samplesToDrawFft];
                int sps = freqSamplesCut.length / samplesToDrawFft;
                for(int i = 0; i < samplesToDrawFft; i++){
                    double avg = 0;
                    for(int o = 0; o < sps; o++){
                        avg += freqSamplesCut[(i*sps)+o];
                    }
                    avg = avg / sps;
                    freqSamplesToDraw[i] = avg;
                }

                grapher.getTimeGraph().setData(buffer);
                grapher.getFreqGraph().setData(freqSamplesToDraw);

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
        JanhoFrequencyAnalyzer jfa = new JanhoFrequencyAnalyzer();
        jfa.begin();
    }
}