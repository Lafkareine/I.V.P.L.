package ivpl.analyzer.nogc;

public class LPC extends ivpl.analyzer.LPC {
	private final double[] temp, r;

	public LPC(int k){
		super(k);
		this.r = new double[k+1];
		this.temp = new double[k+1];
	}

	public ivpl.analyzer.LPC analyze(double[] input){
		for(int i = 0;i<temp.length;i++){
			temp[i] = 0;
		}
		return analyze(input,temp,r,k);
	}

	public double[] residue(double[] inout){
		return super.residue(inout,inout);
	}
}
