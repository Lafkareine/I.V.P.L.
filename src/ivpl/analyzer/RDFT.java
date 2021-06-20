package ivpl.analyzer;

import static java.lang.Math.*;

public abstract class RDFT {

	private final double[] result;
	private final fft4g oura;
	private final int N;

	private double[] real,imaginary,amplitude,phase;

	private boolean real_calc = false;
	private boolean imaginary_calc = false;
	private boolean amplitude_calc = false;
	private boolean phase_calc = false;

	public RDFT(int N){
		assert N >= 2 && Integer.bitCount(N)==1;
		this.N = N;
		result = new double[N];
		oura = new fft4g(N);
	}

	protected RDFT analyze(double[] input){
		System.arraycopy(input,0,result,0, result.length);
		oura.rdft(0,result);
		real_calc = true;
		imaginary_calc = true;
		amplitude_calc = true;
		phase_calc = true;
		return this;
	}

	public double[] getReal() {
		if (real_calc) {
			if (real == null) {
				real = new double[(N >> 1)+1];
			}
			for (int i = 0; i < N; i+=2) {
				real[i>>1] = result[i];
			}
			real[N>>1] = result[1];
			real_calc = false;
		}
		return real;
	}

	public double[] getImaginary() {
		if (imaginary_calc) {
			if (imaginary == null) {
				imaginary = new double[(N >> 1)+1];
			}
			for (int i = 2; i < N; i+=2) {
				imaginary[i>>1] = -result[i+1];
			}
			imaginary_calc = false;
		}
		return imaginary;
	}

	public double[] getAmplitude() {
		if (amplitude_calc) {
			if (amplitude == null) {
				amplitude = new double[(N>>1)+1];
			}
			for (int i = 2; i < N; i+=2) {
				double r = result[i];
				double im = result[i+1];
				amplitude[i>>1] = sqrt(r * r + im * im);
			}
			amplitude_calc = false;
		}
		return amplitude;
	}

	public double[] getPhase() {
		if (phase_calc) {
			if (phase == null) {
				phase = new double[(N>>1)+1];
			}
			for (int i = 2; i < N; i++) {
				double r = real[i];
				double im = imaginary[i];
				phase[N>>1] = atan2(im,r);
			}
			phase_calc = false;
		}
		return phase;
	}

	public double[] getResultRaw() {
		return result;
	}
}
