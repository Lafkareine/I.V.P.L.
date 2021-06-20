package ivpl.io.line;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;

import static javax.sound.sampled.AudioFormat.Encoding.*;

public class Exchanger {

	private final int shift1;
	private final int shift2;
	private final int shift3;
	private static final double RANGE = 0x80_00_00_00L;
	private final int adjust;
	private final int byt;
	private final int step;

	public final AudioFormat format;

	public Exchanger(AudioFormat format) {
		this.format = format;
		if (format.getChannels() != AudioSystem.NOT_SPECIFIED) {
			throw new IllegalArgumentException("Illegal Channel");
		}
		if ((format.getSampleSizeInBits() != AudioSystem.NOT_SPECIFIED) && ((format.getSampleSizeInBits() & 7) == 0)) {
			throw new IllegalArgumentException("Illegal SampleSize");
		}
		if (format.getEncoding() == PCM_SIGNED || format.getEncoding() == PCM_UNSIGNED) {
			int bit = format.getSampleSizeInBits();
			boolean big_endian = format.isBigEndian();
			boolean sign = format.getEncoding() == PCM_SIGNED;
			int a = ((big_endian) ? 24 : 32 - bit);
			int b = ((big_endian) ? -8 : +8);
			shift1 = a;
			shift2 = a + b;
			shift3 = a + b + b;
			adjust = (sign) ? 0 : Integer.MIN_VALUE;
			byt = bit / 8;
			step = (format.getChannels() - 1) * byt;
		} else {
			throw new IllegalArgumentException("Illegal Encoding");
		}
	}

	public byte[] toByteArray(double[][] src, int src_pos, byte[] dest, int dest_pos, int length) {
		int channel = format.getChannels();
		int to = length + src_pos;
		for (int i = 0; i < channel; i++) {
			double[] d = src[i];
			int dest_seek = i * byt + dest_pos;
			for (int j = src_pos; j < to; j++) {
				int sample = ((int) (d[j] * RANGE)) + adjust;
				switch (byt) {
					case 1:
						dest[dest_seek] = (byte) ((sample >>> shift1) & 0xFF);
					case 2:
						dest[dest_seek] = (byte) ((sample >>> shift2) & 0xFF);
					case 3:
						dest[dest_seek] = (byte) ((sample >>> shift3) & 0xFF);
						break;
					default:
						throw new IllegalArgumentException("Illegal bytes");
				}
				dest_seek += step;
			}
		}
		return dest;
	}

	public byte[] toByteArray(double[][] src, int src_pos, int length) {
		return toByteArray(src, src_pos, new byte[length * format.getChannels() * byt], 0, length);
	}

	public double[][] toSample(byte[] src, int src_pos, double[][] dest, int dest_pos, int length) {
		int channel = format.getChannels();
		double r_range = 1.0 / RANGE;
		int to = length + dest_pos;
		for (int i = 0; i < channel; i++) {
			double[] d = dest[i];
			int in_seek = i * byt + src_pos;
			for (int j = dest_pos; j < to; j++) {
				int sample = 0;
				switch (byt) {
					case 1:
						sample |= ((int) src[in_seek++]) << shift1;
					case 2:
						sample |= ((int) src[in_seek++]) << shift2;
					case 3:
						sample |= ((int) src[in_seek++]) << shift3;
						break;
					default:
						throw new IllegalArgumentException("Illegal bytes");
				}
				d[j] = (sample + adjust) * r_range;
				in_seek += step;
			}
		}
		return dest;
	}

	public double[][] toSample(byte[] src, int src_pos,int length) {
		return toSample(src, src_pos, new double[format.getChannels()][length], 0, length);
	}

	public int toDataSize(int length){
		return length * format.getChannels()*byt;
	}

	public int toLength(int data_size){
		return data_size / (format.getChannels()*byt);
	}
}
