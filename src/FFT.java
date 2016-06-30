/**
 * Created by Miquel on 20.6.2016.
 */
public class FFT {

    public static Complex[] fft(Complex[] x) throws IllegalArgumentException {
        int N = x.length;

        if(N == 1) return new Complex[]{x[0]};

        if(!isPowerOfTwo(N)){
            throw new IllegalArgumentException("Complex array must be a length of 2^n");
        }

        //fft for even terms
        Complex[] even = new Complex[N / 2];
        for(int i = 0; i < N/2; i++){
            even[i] = x[2 * i];
        }
        Complex[] q = fft(even);

        //fft for odd terms
        Complex[] odd = even;
        for(int i = 0; i < N/2; i++){
            odd[i] = x[2 * i + 1];
        }
        Complex[] r = fft(odd);

        Complex[] y = new Complex[N];
        for ( int i = 0; i < N / 2; i++){
            double kth = -2 * i * Math.PI / N;
            Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
            y[i] = q[i].plus(wk.times(r[i]));
            y[i + N/2] = q[i].minus(wk.times(r[i]));
        }
        return y;
    }

    public static double[] absFft(double[] samples){
        Complex[] complexSamples = new Complex[samples.length];
        for(int i = 0; i < samples.length; i++){
            complexSamples[i] = new Complex(samples[i], 0);
        }
        complexSamples = FFT.fft(complexSamples);
        double[] absSamples = new double[complexSamples.length];
        for(int i = 0; i < samples.length; i++){
            absSamples[i] = complexSamples[i].getAbs();
        }
        return absSamples;
    }

    private static boolean isPowerOfTwo(int number) throws IllegalArgumentException {
        if(number <0){
            throw new IllegalArgumentException("number: " + number);
        }
        if ((number & -number) == number) {
            return true;
        }
        return false;
    }
}
