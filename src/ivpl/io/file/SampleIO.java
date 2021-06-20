package ivpl.io.file;

import ivpl.io.sample.Sample;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Path;

import static javax.sound.sampled.AudioSystem.*;

public class SampleIO {

	public Sample read(Path path) throws IOException, UnsupportedAudioFileException {
		AudioInputStream stream = getAudioInputStream(AudioFormat.Encoding.PCM_FLOAT,getAudioInputStream(path.toUri().toURL()));
		AudioFormat format = stream.getFormat();
		ByteBuffer buffer = ByteBuffer.wrap(stream.readAllBytes());
		buffer.order(format.isBigEndian()? ByteOrder.BIG_ENDIAN:ByteOrder.LITTLE_ENDIAN);

		int samplen = buffer.capacity()*8/format.getSampleSizeInBits();
		int channels = format.getChannels();

		double[][] samples = new double[channels][samplen];

		switch (format.getSampleSizeInBits()){
			case 32:
				for(int i = 0;i < samplen;i++){
					for(int j = 0;j < channels;j++){
						samples[j][i] = buffer.getFloat();
					}
				}
				break;
			case 64:
				for(int i = 0;i < samplen;i++){
					for(int j = 0;j < channels;j++){
						samples[j][i] = buffer.getDouble();
					}
				}
				break;
			default:
				throw new RuntimeException(new IllegalStateException("未知のバイト数"));
		}

		return new Sample(format.getSampleRate(), samples);
	}

	public void write(double[][] sample, AudioFormat format, AudioFileFormat.Type filetype, Path path) throws IOException {
		final ByteBuffer buffer = ByteBuffer.allocate(8);
		InputStream stream = new InputStream() {

			int i = 0,j = 0,k=8;

			@Override
			public int read() throws IOException {
				if(k == 8) {
					buffer.putDouble(0, sample[j][i]);
					if(++j==sample.length){
						if(++i==sample[0].length){
							return -1;
						}
						j=0;
					}
					k=0;
				}
				return Byte.toUnsignedInt(buffer.get(k++));
			}
		};

		AudioFormat source_format = new AudioFormat(AudioFormat.Encoding.PCM_FLOAT,format.getSampleRate(),64,sample.length,8*sample.length,format.getSampleRate(),buffer.order()==ByteOrder.BIG_ENDIAN);
		AudioInputStream source_stream = new AudioInputStream(stream,source_format,sample[0].length);
		AudioInputStream converted_stream = getAudioInputStream(format,source_stream);
		AudioSystem.write(converted_stream,filetype,path.toFile());

	}
}
