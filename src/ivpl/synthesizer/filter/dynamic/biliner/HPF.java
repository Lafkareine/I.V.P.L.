package ivpl.synthesizer.filter.dynamic.biliner;

import static java.lang.Math.*;

public final class HPF {
	private double o1;
	private double i1;

	public void apply(double[] freq, double[] in, double[] out){
		double o1 = this.o1;
		double i1 = this.i1;

		for(int i = 0;i < out.length;i++) {
			double cos_omega = cos(freq[0] * PI);
			double a = (1.0 - sqrt(1.0 - cos_omega * cos_omega)) / cos_omega;
			double o0 = ((in[0] - i1) * (1 + a)) + (o1 * a * 2) * 0.5;
			i1 = in[0];
			o1 = o0;
			out[i] = o0;
		}

		this.o1 = o1;
		this.i1 = i1;
	}
}
