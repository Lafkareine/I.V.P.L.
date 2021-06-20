package ivpl.synthesizer.filter;

import static java.lang.Math.*;

public final class BiQuadEngine {
private static final double log2div2 = log(2.0) * 0.5;

	public static double alphaByQ(double Q, double omega){
		return sin(omega / (2.0 * Q));
	}

	public static double alphaByBW(double bw, double omega){
		double sin_omega = sin(omega);
		return sin_omega * sinh(log2div2 * bw * omega / sin_omega);
	}

	public static double alphaByS(double S, double A, double omega){
		return sin(omega) * 0.5 * sqrt((A+1.0/A)*(S-1.0/S)+2.0);
	}

}
