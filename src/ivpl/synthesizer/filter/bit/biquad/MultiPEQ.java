package ivpl.synthesizer.filter.bit.biquad;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static ivpl.synthesizer.filter.BiQuadEngine.*;

public class MultiPEQ {
	private final UnionBiQuadEngine engine;
	private double order;

	public MultiPEQ(int order) {
		engine = new UnionBiQuadEngine(order);
		this.order = order;
	}

	public double next(double freq, double A, double bw, double in){

		A /= order;
		bw *= order;

		double omega = 2.0 * PI * freq;
		double alpha = alphaByBW(bw, omega);
		double cos_omega = cos(omega);

		double a0 =  1.0 + alpha / A;
		double a1 = -2.0 * cos_omega;
		double a2 =  1.0 - alpha / A;
		double b0 =  1.0 + alpha * A;
		double b1 = -2.0 * cos_omega;
		double b2 =  1.0 - alpha * A;


		return engine.next(a0, a1, a2, b0, b1, b2, in);
	}
}
