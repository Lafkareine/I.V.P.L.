package ivpl.io.line;

import ivpl.io.sample.SampleInfo;

import javax.sound.sampled.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecordingEnvironment {

	public final List<RecoradingDevice> devices;

	public static class RecoradingDevice {
		public final Mixer mixer;

		public final List<AudioFormat> support;

		private RecoradingDevice(Mixer mixer, List<AudioFormat> support) {
			this.mixer = mixer;
			this.support = Collections.unmodifiableList(support);
		}

		public TargetDataLine getLine() throws LineUnavailableException {
			return (TargetDataLine) mixer.getLine(new Line.Info(TargetDataLine.class));
		}

		public TargetDataLine getLine(AudioFormat format) throws LineUnavailableException {
			return (TargetDataLine) mixer.getLine(new DataLine.Info(TargetDataLine.class, format));
		}

		public EzRecordingLine getEzRecordingLine() throws LineUnavailableException {
			TargetDataLine raw = (TargetDataLine) mixer.getLine(new Line.Info(TargetDataLine.class));
			AudioFormat format = raw.getFormat();
			SampleInfo info = new SampleInfo(format.getChannels(),format.getSampleRate());
			return new EzRecordingLine(raw,info);
		}

		public EzRecordingLine getEzRecordingLine(SampleInfo info) throws LineUnavailableException {

			int max_bit = 0;
			int min_bit = 0;
			SampleInfo inf = new SampleInfo(info.channel, info.sample_rate);

			for (AudioFormat e : support) {
				if (inf.match(e)) {
					max_bit = Integer.max(max_bit, e.getSampleSizeInBits());
					min_bit = Integer.min(min_bit, e.getSampleSizeInBits());

				}
			}
			if (min_bit < 0) {
				max_bit = min_bit;
			}

			for (AudioFormat e : support) {
				if (inf.match(e)) {
					if (e.getSampleSizeInBits() == max_bit)
						return new EzRecordingLine((TargetDataLine) mixer.getLine(new DataLine.Info(TargetDataLine.class, e)), inf);
				}
			}
			throw new RuntimeException("Unexpected error");
		}
	}

	public RecordingEnvironment(Requires requires){
		List<RecoradingDevice> devices = new ArrayList<>();
		for(Mixer.Info mi: AudioSystem.getMixerInfo()){
			Mixer mixer = AudioSystem.getMixer(mi);
			mixer_desc:
			for(Line.Info li :mixer.getTargetLineInfo()){
				if(li.getLineClass() == TargetDataLine.class && li instanceof DataLine.Info){
					DataLine.Info di = (DataLine.Info) li;
					List<AudioFormat> match_formant = new ArrayList<>();
					for(AudioFormat af : di.getFormats()){
						if(requires.match(af)){
							match_formant.add(af);
						}
					}
					if(match_formant.size() > 0){
						devices.add(new RecoradingDevice(mixer,match_formant));
						break mixer_desc;
					}
				}
			}
		}
		this.devices = Collections.unmodifiableList(devices);
	}
}
