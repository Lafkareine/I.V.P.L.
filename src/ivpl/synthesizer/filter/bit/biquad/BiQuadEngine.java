package ivpl.synthesizer.filter.bit.biquad;

public final class BiQuadEngine {
	private double i1, i2, o1, o2;

	public double next(double a0, double a1, double a2, double b0, double b1, double b2, double in){
		double o0=(in*b0+i1*b1+i2*b2-o1*a1-o2*a2)/a0;
		i2=i1;i1=in;
		o2=o1;o1=o0;
		return o0;
	}
}
