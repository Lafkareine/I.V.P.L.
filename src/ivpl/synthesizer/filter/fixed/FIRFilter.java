package ivpl.synthesizer.filter.fixed;

public class FIRFilter {
	private final double[] i;
	private final double[] b;

	public FIRFilter(double[] b){
		this.i = new double[b.length-1];
		this.b = b;
	}

	public double next(double in){
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

	public void apply(double[] in,double[] out){
		for(int i = 0;i<in.length;i++) {
			out[i]=next(in[i]);
		}
	}
}
