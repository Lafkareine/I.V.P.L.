package ivpl.io.line;

import ivpl.io.sample.SampleInfo;

import javax.sound.sampled.*;

public class EzClip extends EzDataLine {

	public final Clip raw;

	public final SampleInfo sample_info;

	public final Exchanger exchanger;

	public EzClip(Clip raw, SampleInfo sample_info) {
		this.raw = raw;
		this.sample_info = sample_info;
		this.exchanger = new Exchanger(raw.getFormat());
	}

	public void open(double[][] data, int offset, int length) throws LineUnavailableException {
		int buffer_size = exchanger.toDataSize(length);
		byte[] buffer = new byte[buffer_size];
		exchanger.toByteArray(data, offset, buffer, 0, length);
		raw.open(raw.getFormat(), buffer, offset, buffer_size);
	}

	public int getFrameLength() {
		return raw.getFrameLength();
	}

	public long getMicrosecondLength() {
		return raw.getMicrosecondLength();
	}

	public void setFramePosition(int frames) {
		raw.setFramePosition(frames);
	}

	public void setMicrosecondPosition(long microseconds) {
		raw.setMicrosecondPosition(microseconds);
	}

	public void setLoopPoints(int start, int end) {
		raw.setLoopPoints(start, end);
	}

	public void loop(int count) {
		raw.loop(count);
	}

	@Override
	protected DataLine raw() {
		return raw;
	}

	@Override
	public SampleInfo getFormat() {
		return sample_info;
	}
}
