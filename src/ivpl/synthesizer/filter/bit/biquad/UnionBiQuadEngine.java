package ivpl.synthesizer.filter.bit.biquad;

public class UnionBiQuadEngine {
	private final double[] state;

	public UnionBiQuadEngine(int order) {
		this.state = new double[order*4];
	}

	public double next(double a0, double a1, double a2, double b0, double b1, double b2, double in){
		double out;
		for(int i = 0;i<state.length;i+=4) {
			double i1=state[i+0],i2=state[i+1],o1=state[i+2],o2=state[i+3];
			out = (in * b0 + i1 * b1 + i2 * b2 - o1 * a1 - o2 * a2) / a0;
			state[i+1] = i1;
			state[i+0] = in;
			state[i+3] = o1;
			state[i+2] = out;
			in = out;
		}
		return in;
	}
}
