package ivpl.synthesizer.filter.fixed;

public class IRFilter {
	private final double[] i,o;
	private final double[] a,b;

	public IRFilter(double[] a, double[] b){
		this.i = new double[b.length];
		this.o = new double[a.length];
		this.a = a;
		this.b = b;
	}

	public double next(double in){
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

	public void apply(double[] in,double[] out){
		for(int i = 0;i<in.length;i++) {
			out[i]=next(in[i]);
		}
	}
}
