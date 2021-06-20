package ivpl.analyzer.safe;

public class RDFT extends ivpl.analyzer.RDFT {

	public RDFT(double[] sample) {
		super(sample.length);
		analyze(sample);
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

	@Override
	public double[] getResultRaw() {
		return ArrayCopy.copyArray(super.getResultRaw());
	}
}
