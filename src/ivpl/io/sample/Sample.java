package ivpl.io.sample;

public final class Sample {
	public final float sample_rate;
	public final double[][] samples;

	public Sample(float sample_rate, double[][] samples) {
		this.sample_rate = sample_rate;
		this.samples = samples;
	}

	public SampleInfo getInfo(){
		return new SampleInfo(samples.length,sample_rate);
	}

	public int length() {
		return samples[0].length;
	}
}
