import org.sintef.jarduino.DigitalPin;
import org.sintef.jarduino.DigitalState;
import org.sintef.jarduino.JArduino;
import org.sintef.jarduino.PinMode;

import javax.sound.sampled.LineUnavailableException;
import java.util.Scanner;

/**
 * Created by Miquel on 20.6.2016.
 */

public class JanhoFrequencyAnalyzer extends JArduino {

    private FFT fft;
    private AudioSampler audioSampler;
    Grapher grapher;

    int samplesPerUpdate = 512;
    int bufferSize = 16384;
    int samplesForFft = 4096; //must be 2^n
    int samplesCutAfterFft = 0;
    int samplesToDrawTime = 2024; //must be bufferSize / n;
    int samplesToDrawFft = 256; //must fulfill: samplesToDrawFft * n = samplesForFft / 2, n = {1, 2, 3 ...}
    double[] buffer = new double[bufferSize];

    DigitalPin ledPin = DigitalPin.PIN_5;
    int ledControlFromWhatFreqsamplesToDrawValue = 3;
    double ledBaseTreshold = 5000;


    public JanhoFrequencyAnalyzer(String port) {
        super(port);
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
        JanhoFrequencyAnalyzer jfa = new JanhoFrequencyAnalyzer("COM3");
        jfa.runArduinoProcess();
    }

    @Override
    protected void setup() {
        this.pinMode(ledPin, PinMode.OUTPUT);
        Scanner scanner = new Scanner(System.in);

        grapher = new Grapher();
        grapher.getFreqGraph().setMaxValue(samplesForFft * grapher.getFreqGraph().getMaxValue());

        fft = new FFT();
        audioSampler = new AudioSampler();

        while(!audioSampler.lineChosen()) {
            try {
                audioSampler.chooseLineLUI(scanner);
            } catch (LineUnavailableException lue) {
                lue.printStackTrace();
            }
        }

        try {
            audioSampler.openLine();
        } catch(LineUnavailableException lue){
            lue.printStackTrace();
        }
    }

    @Override
    protected void loop() {
        try {
            saveSamplesToBuffer(audioSampler.getSamplesDouble(samplesPerUpdate));

            double[] timeSamplesToDraw = new double[samplesToDrawTime];
            int sps = buffer.length / timeSamplesToDraw.length;
            for (int i = 0; i < timeSamplesToDraw.length; i++) {
                timeSamplesToDraw[i] = buffer[i * sps];
            }

            double[] freqSamples = new double[samplesForFft];
            for (int i = 0; i < samplesForFft; i++) {
                freqSamples[i] = buffer[buffer.length - samplesForFft + i];
            }
            freqSamples = FFT.absFft(freqSamples);


            //split in half becouse the spectrum is twofold
            double[] freqSamplesCut = new double[freqSamples.length / 2 - samplesCutAfterFft];
            for (int i = 0; i < freqSamplesCut.length; i++) {
                freqSamplesCut[i] = freqSamples[i];
            }

            double[] freqSamplesToDraw = new double[samplesToDrawFft];
            sps = freqSamplesCut.length / samplesToDrawFft;
            for (int i = 0; i < samplesToDrawFft; i++) {
                double avg = 0;
                for (int o = 0; o < sps; o++) {
                    avg += freqSamplesCut[(i * sps) + o];
                }
                avg = avg / sps;
                freqSamplesToDraw[i] = avg;
            }

            System.out.println(freqSamplesToDraw[ledControlFromWhatFreqsamplesToDrawValue]);
            if (freqSamplesToDraw[ledControlFromWhatFreqsamplesToDrawValue] > ledBaseTreshold){
                this.digitalWrite(ledPin, DigitalState.HIGH);
            } else {
                this.digitalWrite(ledPin, DigitalState.LOW);
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