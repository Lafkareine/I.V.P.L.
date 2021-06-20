package ivpl.io.line;

import javax.sound.sampled.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StreamingEnvironment {

	public final List<StreamingDevice> devices;

	public static class StreamingDevice {
		public final Mixer mixer;

		public final List<AudioFormat> support;

		private StreamingDevice(Mixer mixer, List<AudioFormat> support) {
			this.mixer = mixer;
			this.support = Collections.unmodifiableList(support);
		}


		public SourceDataLine getLine(AudioFormat format) throws LineUnavailableException {
			return (SourceDataLine) mixer.getLine(new DataLine.Info(SourceDataLine.class, format));
		}

		public SourceDataLine getLine() throws LineUnavailableException {
			return (SourceDataLine) mixer.getLine(new Line.Info(SourceDataLine.class));
		}
	}

	public StreamingEnvironment(Requires requires){
		List<StreamingDevice> devices = new ArrayList<>();
		for(Mixer.Info mi: AudioSystem.getMixerInfo()){
			Mixer mixer = AudioSystem.getMixer(mi);
			mixer_desc:
			for(Line.Info li :mixer.getSourceLineInfo()){
				if(li.getLineClass() == SourceDataLine.class && li instanceof DataLine.Info){
					DataLine.Info di = (DataLine.Info) li;
					List<AudioFormat> match_formant = new ArrayList<>();
					for(AudioFormat af : di.getFormats()){
						if(requires.match(af)){
							match_formant.add(af);
						}
					}
					if(match_formant.size() > 0){
						devices.add(new StreamingDevice(mixer,match_formant));
						break mixer_desc;
					}
				}
			}
		}
		this.devices = Collections.unmodifiableList(devices);
	}
}
