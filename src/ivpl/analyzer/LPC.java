package ivpl.analyzer;

public abstract class LPC {

	private double e;
	private final double[] a;
	public final int k;

	public LPC(int k) {
		this.k = k;
		this.a = new double[k+1];
	}

	protected final LPC analyze(double[] input, double[] temp, double[] r, int k){
		for(int i = 0;i<=k;i++){
			r[i] = auto_correlation(input,i);
		}
		a[0] = 1;
		a[1] = -r[1]/r[0];
		double e = r[0]+a[1]*r[1];
		levinson_durbin_recursion(a,e,1,r,temp);
		return this;
	}

	private double auto_correlation(double[] y, int l){
		double sigma = 0;
		for(int j = l; j < y.length;j++){
			int i = j-l;
			sigma += y[i]*y[j];
		}
		return sigma;
	}

	private void levinson_durbin_recursion(double[] a, double e, int k, double[] r, double[] temp){
		int _k = k+1;

		//lambdaの導出
		double sigma = 0;
		for(int i = 0;i <= k;i++){
			sigma += a[i] + r[k+1-i];
		}
		double lambda = -sigma / e;

		//aの更新
		System.arraycopy(a,0,temp,0,k+1);
		for(int i = 0;i <= _k;i++){
			a[i] = temp[i]+lambda*temp[_k-i];
		}

		//eの更新
		double _e = 1-lambda*lambda * e;

		//k(LPCの次数)+1の配列がすべて埋まるまで再起
		if(_k < a.length-1) {
			levinson_durbin_recursion(a,_e,_k,r,temp);
		}else{
			this.e = _e;
		}
	}

	public final double[] residue(double[] input, double[] receptacle){
		double[] a = this.a;
		for(int i = input.length-1;i>0;i--){
			double sigma = 0;
			for(int j = 1;j<=k&&j<=i;j++){
				sigma = input[i-j] * a[j];
			}
			//http://www.kumikomi.net/archives/2010/08/ep30gose.php?page=2
			receptacle[i] = input[i]+sigma;
			//residue[i] = input[i-sigma;
		}
		return receptacle;
	}

	public final double getE() {
		return e;
	}

	public double[] getA() {
		return a;
	}
}
