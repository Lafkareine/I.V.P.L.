package ivpl.io.line;

import ivpl.io.sample.SampleInfo;

import javax.sound.sampled.*;

public class EzStreamingLine extends EzDataLine{

	public final SourceDataLine raw;

	private final SampleInfo sample_info;

	private final Exchanger exchanger;

	private byte[] buffer;

	public EzStreamingLine(SourceDataLine raw, SampleInfo sample_info) {
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

	public int write(double[][] d, int off, int len) {
		int r = 0;
		int buf_len = exchanger.toLength(buffer.length);
		int to_data_size = exchanger.toDataSize(1);
		while (r < len){
			int req = Integer.min(len-r,buf_len);
			exchanger.toByteArray(d,off+r,buffer,0,req);
			int written = raw.write(buffer,0,req*to_data_size)/to_data_size;
			r += written;
			if(written != req){break;}
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
