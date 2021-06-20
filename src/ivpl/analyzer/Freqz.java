package ivpl.analyzer;

import static java.lang.Math.*;


public abstract class Freqz {

	private static final double pi2 = PI + PI;

	private final double[] imaginary;
	private final double[] real;

	private double[] amplitude, phase;

	private boolean amplitude_calc = false;
	private boolean phase_calc = false;

	public final int n;

	public Freqz(int n) {
		this.n = n;
		imaginary = new double[n];
		real = new double[n];
	}

	protected Freqz analyse(double[] a, double[] b, double[] freq_pps) {
		amplitude_calc = true;
		phase_calc = true;
		for (int i = 0; i < n; i++) {

			double ipi2 = pi2 * freq_pps[i];

			double n_i = 0, n_r, d_i = 0, d_r;

			if (b != null) {
				n_r = 0;
				for (int j = 0; j < b.length; j++) {
					double ijpi2 = ipi2 * j;
					double bj = b[j];
					n_r += cos(ijpi2) * bj;
					n_i += sin(ijpi2) * bj;
				}
				n_r /= b.length;
				n_i /= b.length;
			} else {
				n_r = 1;
			}

			if (a != null) {
				d_r = 0;
				for (int j = 0; j < a.length; j++) {
					double ijpi2 = ipi2 * j;
					double aj = a[j];
					d_r += cos(ijpi2) * aj;
					d_i += sin(ijpi2) * aj;
				}
				d_r /= a.length;
				d_i /= a.length;
			} else {
				this.real[i] = n_r;
				this.imaginary[i] = n_i;
				return this;
			}

			double div = d_r * d_r + d_i * d_i;
			double real = (d_r * n_r + d_i * n_i) / div;
			double image = (d_r * n_i + d_i * n_r) / div;

			this.real[i] = real;
			this.imaginary[i] = image;
		}
		return this;
	}

	public double[] getAmplitude() {
		if (amplitude_calc) {
			if (amplitude == null) {
				amplitude = new double[n];
			}
			for (int i = 0; i < n; i++) {
				double r = real[i];
				double im = imaginary[i];
				amplitude[i] = sqrt(r * r + im * im);
			}
		}
		return amplitude;
	}

	public double[] getPhase() {
		if (phase_calc) {
			if (phase == null) {
				phase = new double[n];
			}
			for (int i = 0; i < n; i++) {
				double r = real[i];
				double im = imaginary[i];
				phase[i] = atan2(im,r);
			}
		}
		return phase;
	}

	public double[] getReal() {
		return real;
	}

	public double[] getImaginary() {
		return imaginary;
	}

	public static double[] makeFreqArrayLiner(double min_pps, double max_pps, int N){
		return makeFreqArrayLiner(min_pps, max_pps, new double[N]);
	}

	public static double[] makeFreqArrayLiner(double min_pps, double max_pps, double[] receptacle){
		int a = receptacle.length-1;
		for(int i = 1;i<receptacle.length-1;i++){
			receptacle[i] = (max_pps * i + min_pps * (a - i)) / a;
		}
		receptacle[0] = min_pps;
		receptacle[receptacle.length-1] = max_pps;
		return receptacle;
	}

	public static double[] makeFreqArrayLog(double min_pps, double max_pps, int N){
		return makeFreqArrayLog(min_pps, max_pps, new double[N]);
	}

	public static double[] makeFreqArrayLog(double min_pps, double max_pps, double[] receptacle){
		int a = receptacle.length-1;
		double b = Math.log(min_pps);
		double c = Math.log(max_pps);
		for(int i = 1;i<receptacle.length-1;i++){
			receptacle[i] = exp((c * i + b * (a - i)) / a);
		}
		receptacle[0] = min_pps;
		receptacle[receptacle.length-1] = max_pps;
		return receptacle;
	}
}
