package ivpl.synthesizer.filter.bit.biquad;

import static java.lang.Math.*;
import static ivpl.synthesizer.filter.BiQuadEngine.*;

public final class LPF {
	private final BiQuadEngine engine = new BiQuadEngine();
	private static final double SQRT_HALF = sqrt(0.5);

	public double next(double freq, double Q, double in) {

		double omega = 2.0 * PI * freq;
		double alpha = alphaByQ(Q, omega);
		double cos_omega = cos(omega);
		double a0 = 1.0 + alpha;
		double a1 = -2.0 * cos_omega;
		double a2 = 1.0 - alpha;
		double b0 = (1.0 - cos_omega) / 2.0;
		double b1 = 1.0 - cos_omega;
		double b2 = (1.0 - cos_omega) / 2.0;
		return engine.next(a0, a1, a2, b0, b1, b2, in);
	}

	public double next(double freq, double in) {
		return next(freq, SQRT_HALF, in);
	}
}
