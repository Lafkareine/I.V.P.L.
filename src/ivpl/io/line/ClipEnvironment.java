package ivpl.io.line;

import ivpl.io.sample.Sample;
import ivpl.io.sample.SampleInfo;

import javax.sound.sampled.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClipEnvironment {

	public final List<ClipDevice> devices;

	public static class ClipDevice {
		public final Mixer mixer;

		public final List<AudioFormat> support;

		private ClipDevice(Mixer mixer, List<AudioFormat> support) {
			this.mixer = mixer;
			this.support = Collections.unmodifiableList(support);
		}

		public Clip getClip(AudioFormat format) throws LineUnavailableException {
			return (Clip) mixer.getLine(new DataLine.Info(Clip.class, format));
		}

		public Clip getClip() throws LineUnavailableException {
			return (Clip) mixer.getLine(new Line.Info(Clip.class));
		}

		public EzClip getEzClip(Sample sample) throws LineUnavailableException {
			return getEzClip(sample,0, sample.length());
		}

		public EzClip getEzClip(Sample sample, int offset, int length) throws LineUnavailableException {
			SampleInfo info = sample.getInfo();
			EzClip clip = getEzClip(info);
			clip.open(sample.samples,offset,length*info.channel*8);
			return clip;
		}

		public EzClip getEzClip() throws LineUnavailableException {
			Clip raw = (Clip) mixer.getLine(new Line.Info(Clip.class));
			AudioFormat format = raw.getFormat();
			SampleInfo info = new SampleInfo(format.getChannels(),format.getSampleRate());
			return new EzClip(raw,info);
		}

		public EzClip getEzClip(SampleInfo info) throws LineUnavailableException {

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
						return new EzClip((Clip) mixer.getLine(new DataLine.Info(Clip.class, e)), inf);
				}
			}
			throw new RuntimeException("Unexpected error");
		}
	}

	public ClipEnvironment(Requires requires) {
		List<ClipDevice> devices = new ArrayList<>();
		for (Mixer.Info mi : AudioSystem.getMixerInfo()) {
			Mixer mixer = AudioSystem.getMixer(mi);
			mixer_desc:
			for (Line.Info li : mixer.getSourceLineInfo()) {
				if (li.getLineClass() == EzClip.class && li instanceof DataLine.Info) {
					DataLine.Info di = (DataLine.Info) li;
					List<AudioFormat> match_formant = new ArrayList<>();
					for (AudioFormat af : di.getFormats()) {
						if (requires.match(af)) {
							match_formant.add(af);
						}
					}
					if (match_formant.size() > 0) {
						devices.add(new ClipDevice(mixer, match_formant));
						break mixer_desc;
					}
				}
			}
		}
		this.devices = Collections.unmodifiableList(devices);
	}
}
