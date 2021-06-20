package ivpl.synthesizer.filter.fixed;

public class IIRFilter {
	private final double[] o;
	private final double[] a;

	public IIRFilter(double[] a){
		this.o = new double[a.length-1];
		this.a = a;
	}

	public double next(double in){
		double o0 = in;

		for(int i = 0;i<this.o.length;i++){
			this.o[i] -= this.o[i]*a[i+1];
		}
		o0/=a[0];
		for(int i = this.o.length-1;i>0;i++){
			this.o[i] = this.o[i-1];
		}
		o[0] = o0;
		return o0;
	}

	public void apply(double[] in,double[] out){
		for(int i = 0;i<in.length;i++) {
			out[i]=next(in[i]);
		}
	}
}
