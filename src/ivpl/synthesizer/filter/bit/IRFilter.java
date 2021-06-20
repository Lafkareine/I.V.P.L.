package ivpl.synthesizer.filter.bit;

public class IRFilter {
	private final double[] i,o;

	public IRFilter(int a_n, int b_n){
		this.i = new double[b_n-1];
		this.o = new double[a_n-1];
	}

	public double next(double[] a, double[] b, double in){
		double o0 = in*b[0];

		for(int i = 0;i<this.i.length;i++){
			o0 += this.i[i]*b[i+1];
		}
		for(int i = 0;i<this.o.length;i++){
			this.o[i] -= this.o[i]*a[i+1];
		}
		o0/=a[0];
		for(int i = this.i.length-1;i>0;i++){
			this.i[i] = this.i[i-1];
		}
		for(int i = this.o.length-1;i>0;i++){
			this.o[i] = this.o[i-1];
		}
		i[0] = in;
		o[0] = o0;
		return o0;
	}

	public void apply(double[] a, double[] b, double[] in, double[] out){
		for(int i = 0;i<in.length;i++) {
			out[i] = next(a,b,in[i]);
		}
	}
}
