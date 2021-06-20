package ivpl.synthesizer.filter.dynamic.biliner;

import static java.lang.Math.*;

public final class LPF {

	private double o1;

	public void apply(double[] freq, double[] in, double[] out){
		double o1 = this.o1;

		for(int i = 0;i < out.length;i++) {
			double cos_omega = cos(freq[0] * PI);
			double a1 = -sqrt((cos_omega-1.0)*(cos_omega-3.0))-cos_omega+2.0;
			double b0 = 1.0-a1;
			double o0 = o1*a1+in[0]*b0;
			o1=o0;
			out[i] = o0;
		}

		this.o1 = o1;
	}
}
