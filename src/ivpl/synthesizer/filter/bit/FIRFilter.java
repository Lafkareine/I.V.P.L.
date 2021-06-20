package ivpl.synthesizer.filter.bit;

public class FIRFilter {
	private final double[] i;

	public FIRFilter(int b_n){
		this.i = new double[b_n-1];
	}

	public double next(double[] b, double in){
		double o0 = in*b[0];
		for(int i = 0;i<this.i.length;i++){
			o0 += this.i[i]*b[i+1];
		}
		for(int i = this.i.length-1;i>0;i++){
			this.i[i] = this.i[i-1];
		}
		i[0] = in;
		return o0;
	}

	public void apply(double[] b, double[] in, double[] out){
		for(int i = 0;i<in.length;i++) {
			out[i] = next(b,in[i]);
		}
	}
}
