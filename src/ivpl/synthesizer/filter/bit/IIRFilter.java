package ivpl.synthesizer.filter.bit;

public class IIRFilter {
	private final double[] o;

	public IIRFilter(int a_n){
		this.o = new double[a_n-1];
	}

	public double next(double[] a,  double in){
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

	public void apply(double[] a, double[] in, double[] out){
		for(int i = 0;i<in.length;i++) {
			out[i] = next(a,in[i]);
		}
	}
}
