package ivpl.analyzer.safe;

public class Freqz extends ivpl.analyzer.Freqz {

	public Freqz(double[] a, double[] b, double[] freq_pps) {
		super(freq_pps.length);
		analyse(a,b,freq_pps);
	}

	@Override
	public double[] getReal() {
		return ArrayCopy.copyArray(super.getReal());
	}

	@Override
	public double[] getImaginary() {
		return ArrayCopy.copyArray(super.getImaginary());
	}

	@Override
	public double[] getAmplitude() {
		return ArrayCopy.copyArray(super.getAmplitude());
	}

	@Override
	public double[] getPhase() {
		return ArrayCopy.copyArray(super.getPhase());
	}
}
