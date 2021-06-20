package ivpl.io.line;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;

public class Requires {
	public final int min_channels, max_channel;
	public final float[] sample_rate;

	public Requires(int min_channels, int max_channel, float... sample_rate) {
		this.min_channels = min_channels;
		this.max_channel = max_channel;
		this.sample_rate = sample_rate;
	}

	public Requires(int min_channels, int max_channel) {
		this.min_channels = min_channels;
		this.max_channel = max_channel;
		this.sample_rate = null;
	}

	public Requires(float... sample_rate) {
		this.min_channels = 1;
		this.max_channel = Integer.MAX_VALUE;
		this.sample_rate = sample_rate;
	}

	public Requires() {
		this.min_channels = 1;
		this.max_channel = Integer.MAX_VALUE;
		this.sample_rate = null;
	}

	public boolean match(AudioFormat format){
		if((format.getChannels() == AudioSystem.NOT_SPECIFIED) || ((min_channels <= format.getChannels())&&(format.getChannels()<=max_channel))){
			if((sample_rate==null)||(format.getSampleRate() == AudioSystem.NOT_SPECIFIED)){
				return true;
			}else {
				for (float e:sample_rate){
					if(e == format.getSampleRate()){
						return true;
					}
				}
			}
		}
		return false;
	}
}
