package core.webui.server.handlers.renderedobjects;

import java.util.List;

import core.config.Config;
import core.recorder.Recorder;

public final class RenderedConfig {
	private final boolean recordMouseClickOnly;
	private final boolean executeOnRelease;
	private final boolean useClipboardToTypeString;
	private final List<RenderedDebugLevel> debugLevels;

	public RenderedConfig(Config config, Recorder recorder) {
		executeOnRelease = config.isExecuteOnKeyReleased();
		useClipboardToTypeString = config.isUseClipboardToTypeString();
		recordMouseClickOnly = recorder.getRecordMode() == Recorder.MODE_MOUSE_CLICK_ONLY;
		debugLevels = RenderedDebugLevel.of(config.getNativeHookDebugLevel());
	}

	public boolean isRecordMouseClickOnly() {
		return recordMouseClickOnly;
	}

	public boolean isExecuteOnRelease() {
		return executeOnRelease;
	}

	public boolean isUseClipboardToTypeString() {
		return useClipboardToTypeString;
	}

	public List<RenderedDebugLevel> getDebugLevels() {
		return debugLevels;
	}

}
