package ivpl.io.sample;

import javax.sound.sampled.AudioFormat;
import java.util.Objects;

public final class SampleInfo {
	public final int channel;
	public final float sample_rate;

	public SampleInfo(int channel, float sample_rate) {
		this.channel = channel;
		this.sample_rate = sample_rate;
	}

	public boolean match(AudioFormat format){
		return channel == format.getChannels() && sample_rate == format.getSampleRate();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SampleInfo that = (SampleInfo) o;
		return channel == that.channel && Float.compare(that.sample_rate, sample_rate) == 0;
	}

	@Override
	public int hashCode() {
		int result = channel;
		result = 31 * result + (sample_rate != +0.0f ? Float.floatToIntBits(sample_rate) : 0);
		return result;
	}
}
