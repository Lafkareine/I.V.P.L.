package ivpl.synthesizer.filter.bit.biquad;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static ivpl.synthesizer.filter.BiQuadEngine.*;

public final class TwinBandPass {
	private final BiQuadEngine first = new BiQuadEngine();
	private final BiQuadEngine second = new BiQuadEngine();

	public double next(double freq, double bw, double in){

		double omega = 2.0f * PI * freq;
		double alpha = alphaByBW(bw,omega);

		double a0 =  1.0f + alpha;
		double a1 = -2.0f * cos(omega);
		double a2 =  1.0f - alpha;
		double b0 =  alpha;
		double b1 =  0.0f;
		double b2 = -alpha;
		return second.next(a0, a1, a2, b0, b1, b2, first.next(a0, a1, a2, b0, b1, b2, in));
	}

	public double next(double freq, double in){
		return next(freq,1.0, in);
	}
}
