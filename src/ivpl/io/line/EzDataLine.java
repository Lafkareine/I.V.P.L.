package ivpl.io.line;

import ivpl.io.sample.SampleInfo;

import javax.sound.sampled.Control;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineListener;

public abstract class EzDataLine {
	
	protected abstract DataLine raw();

	public abstract SampleInfo getFormat();

	public void drain() {
		raw().drain();
	}

	
	public void flush() {
		raw().flush();
	}

	
	public void start() {
		raw().start();
	}

	
	public void stop() {
		raw().stop();
	}

	
	public boolean isRunning() {
		return raw().isRunning();
	}

	
	public boolean isActive() {
		return raw().isActive();
	}
	
	public int getBufferFrameSize() {
		return raw().getBufferSize() / (8 * getFormat().channel);
	}

	
	public int available() {
		return raw().available();
	}

	
	public int getFramePosition() {
		return raw().getFramePosition();
	}

	
	public long getLongFramePosition() {
		return raw().getLongFramePosition();
	}

	
	public long getMicrosecondPosition() {
		return raw().getMicrosecondPosition();
	}

	
	public float getLevel() {
		return raw().getLevel();
	}

	public void close() {
		raw().close();
	}

	
	public boolean isOpen() {
		return raw().isOpen();
	}

	
	public Control[] getControls() {
		return raw().getControls();
	}

	
	public boolean isControlSupported(Control.Type control) {
		return raw().isControlSupported(control);
	}

	
	public Control getControl(Control.Type control) {
		return raw().getControl(control);
	}

	
	public void addLineListener(LineListener listener) {
		raw().addLineListener(listener);
	}

	
	public void removeLineListener(LineListener listener) {
		raw().removeLineListener(listener);
	}
}
