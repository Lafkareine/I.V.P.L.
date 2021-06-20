package ivpl.synthesizer.filter.bit.biliner;

import static java.lang.Math.*;

public final class HPF {
	private double o1;
	private double i1;

	public double next(double freq, double in){
		double cos_omega = cos(freq * PI);
		double a = (1.0-sqrt(1.0-cos_omega*cos_omega))/cos_omega;
		double o0 = ((in-i1)*(1+a))+(o1*a*2)*0.5;
		i1 = in;
		o1 = o0;
		return o0;
	}
}
