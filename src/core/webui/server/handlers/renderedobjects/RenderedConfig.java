package core.webui.server.handlers.renderedobjects;

import java.util.List;

import core.config.Config;
import core.recorder.Recorder;

public final class RenderedConfig {
	private final boolean recordMouseClickOnly;
	private final boolean haltTaskByEscape;
	private final boolean executeOnRelease;
	private final boolean useClipboardToTypeString;
	private final boolean runTaskWithServerConfig;
	private final boolean useJavaAwtToGetMousePosition;
	private final boolean useTrayIcon;
	private final List<RenderedDebugLevel> debugLevels;

	public RenderedConfig(Config config, Recorder recorder) {
		haltTaskByEscape = config.isEnabledHaltingKeyPressed();
		executeOnRelease = config.isExecuteOnKeyReleased();
		useClipboardToTypeString = config.isUseClipboardToTypeString();
		runTaskWithServerConfig = config.isRunTaskWithServerConfig();
		recordMouseClickOnly = recorder.getRecordMode() == Recorder.MODE_MOUSE_CLICK_ONLY;
		useJavaAwtToGetMousePosition = config.isUseJavaAwtToGetMousePosition();
		useTrayIcon = config.isUseTrayIcon();
		debugLevels = RenderedDebugLevel.of(config.getNativeHookDebugLevel());
	}

	public boolean isRecordMouseClickOnly() {
		return recordMouseClickOnly;
	}

	public boolean isHaltTaskByEscape() {
		return haltTaskByEscape;
	}

	public boolean isExecuteOnRelease() {
		return executeOnRelease;
	}

	public boolean isUseClipboardToTypeString() {
		return useClipboardToTypeString;
	}

	public boolean isRunTaskWithServerConfig() {
		return runTaskWithServerConfig;
	}

	public boolean isUseJavaAwtToGetMousePosition() {
		return useJavaAwtToGetMousePosition;
	}

	public boolean isUseTrayIcon() {
		return useTrayIcon;
	}

	public List<RenderedDebugLevel> getDebugLevels() {
		return debugLevels;
	}

}
