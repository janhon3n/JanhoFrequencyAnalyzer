import javax.sound.sampled.LineUnavailableException;
import java.util.Scanner;

/**
 * Created by Miquel on 20.6.2016.
 */
public class JanhoFrequencyAnalyzer {

    private FFT fft;
    private AudioSampler audioSampler;
    Grapher grapher;

    int samplesPerUpdate = 512;
    int bufferSize = 16384;
    int samplesForFft = 4096; //must be 2^n
    int samplesToDrawTime = 2024; //must be bufferSize / n;
    int samplesToDrawFft = 256; //must fulfill: samplesToDrawFft * n = samplesForFft / 2, n = {1, 2, 3 ...}
    double[] buffer = new double[bufferSize];

    float lowesFreqToGraph = 200;

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
                saveSamplesToBuffer(audioSampler.getSamplesDouble(samplesPerUpdate));

                double[] timeSamplesToDraw = new double[samplesToDrawTime];
                int sps = buffer.length / timeSamplesToDraw.length;
                for(int i = 0; i < timeSamplesToDraw.length; i++){
                    timeSamplesToDraw[i] = buffer[i * sps];
                }

                double[] freqSamples = new double[samplesForFft];
                for(int i = 0; i < samplesForFft; i++){
                    freqSamples[i] = buffer[buffer.length - samplesForFft + i];
                }
                freqSamples = FFT.absFft(freqSamples);
                //cut the sample size in half becouse the spectrum is 2 sided
                double[] freqSamplesCut = new double[freqSamples.length / 2];
                for(int i = 0; i < freqSamplesCut.length; i++){
                    freqSamplesCut[i] = freqSamples[i];
                }

                double deltaF = (audioSampler.getSampleRate() / 2) / (freqSamplesCut.length / 2);
                int samplesCutFromTheLow = (int) (lowesFreqToGraph / deltaF);

                double indexIncreasement = Math.log10(freqSamplesCut.length) / samplesToDrawFft;

                int freqSamplesCutFromTheLow = (int) (Math.log10(samplesCutFromTheLow) / indexIncreasement);

                double[] freqSamplesToDraw = new double[samplesToDrawFft - freqSamplesCutFromTheLow];
                for(int i = 0; i < samplesToDrawFft - freqSamplesCutFromTheLow; i++) {
                    int timeSampleIndex = (int) Math.pow(10, indexIncreasement * (i + freqSamplesCutFromTheLow));
                    //find out how many samples are between the last index and this one and sum them in to the final freq data which is drawn
                    int sampleCountToSum = (int) (Math.pow(10, indexIncreasement * (i + freqSamplesCutFromTheLow)) - Math.pow(10, indexIncreasement * (i - 1 + freqSamplesCutFromTheLow)));

                    if(sampleCountToSum < 1){

                        sampleCountToSum = 1;
                    }

                    double sum = 0;
                    for (int o = 0; o < sampleCountToSum; o++) {
                        sum += freqSamplesCut[(int) Math.pow(10, indexIncreasement * (i + freqSamplesCutFromTheLow)) - o];
                    }
                    freqSamplesToDraw[i] = sum;
                }

                grapher.getTimeGraph().setData(timeSamplesToDraw);
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