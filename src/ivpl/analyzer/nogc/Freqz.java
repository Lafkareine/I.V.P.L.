package ivpl.analyzer.nogc;

public class Freqz extends ivpl.analyzer.Freqz {

	public Freqz(int n) {
		super(n);
	}

	@Override
	public ivpl.analyzer.Freqz analyse(double[] a, double[] b, double[] freq_pps) {
		return super.analyse(a, b, freq_pps);
	}
}
