package ivpl.io.line;

import ivpl.io.sample.SampleInfo;

import javax.sound.sampled.*;

public class EzRecordingLine extends EzDataLine{

	private final TargetDataLine raw;

	private final SampleInfo sample_info;

	public final Exchanger exchanger;

	private byte[] buffer;

	public EzRecordingLine(TargetDataLine raw, SampleInfo sample_info) {
		this.raw = raw;
		this.sample_info = sample_info;
		exchanger = new Exchanger(raw.getFormat());
	}

	public void open() throws LineUnavailableException {
		raw.open(raw.getFormat());
		buffer = new byte[raw.getBufferSize()];
	}

	public void open(int buffer_length) throws LineUnavailableException {
		int buffer_size = exchanger.toDataSize(buffer_length);
		raw.open(raw.getFormat(),buffer_size);
		buffer = new byte[buffer_size];
	}

	public int read(double[][] d, int off, int len) {
		int r = 0;
		int buf_len = exchanger.toLength(buffer.length);
		int to_data_size = exchanger.toDataSize(1);
		while (r < len){
			int req = Integer.min(len-r,buf_len);
			int red = raw.read(buffer,0,req*to_data_size)/to_data_size;
			exchanger.toSample(buffer,0,d,off+r,red);
			r += red;
			if(red != req){break;}
		}
		return r;
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
