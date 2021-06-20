package ivpl.analyzer.safe;

public class LPC extends ivpl.analyzer.LPC {

	public LPC(double[] input, int k){
		super(k);
		analyze(input, new double[k+1],new double[k+1],1);
	}

	public double[] residue(double[] input){
		double[] residue = new double[input.length];
		return super.residue(input,residue);
	}

	public double[] getA() {
		return ArrayCopy.copyArray(super.getA());
	}
}
