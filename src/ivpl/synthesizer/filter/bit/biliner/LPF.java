package ivpl.synthesizer.filter.bit.biliner;

import static java.lang.Math.*;

public final class LPF {

	private double o1;

	public double next(double freq, double in){
		double cos_omega = cos(freq * PI);
		double a1 = -sqrt((cos_omega-1.0)*(cos_omega-3.0))-cos_omega+2.0;
		double b0 = 1.0-a1;
		double o0 = o1*a1+in*b0;
		o1=o0;
		return o0;
	}
}
