package core.webui.server.handlers.renderedobjects;

import java.util.List;

import core.recorder.Recorder;

import static frontEnd.Backend.CONFIG;
import static frontEnd.Backend.RECORDER;

public final class RenderedConfig {
	private final boolean recordMouseClickOnly;
	private final boolean haltTaskByEscape;
	private final boolean executeOnRelease;
	private final boolean useClipboardToTypeString;
	private final boolean runTaskWithServerConfig;
	private final boolean useJavaAwtToGetMousePosition;
	private final boolean useTrayIcon;
	private final List<RenderedDebugLevel> debugLevels;

	public RenderedConfig() {
		haltTaskByEscape = CONFIG.isEnabledHaltingKeyPressed();
		executeOnRelease = CONFIG.isExecuteOnKeyReleased();
		useClipboardToTypeString = CONFIG.isUseClipboardToTypeString();
		runTaskWithServerConfig = CONFIG.isRunTaskWithServerConfig();
		recordMouseClickOnly = RECORDER.getRecordMode() == Recorder.MODE_MOUSE_CLICK_ONLY;
		useJavaAwtToGetMousePosition = CONFIG.isUseJavaAwtToGetMousePosition();
		useTrayIcon = CONFIG.isUseTrayIcon();
		debugLevels = RenderedDebugLevel.of(CONFIG.getNativeHookDebugLevel());
	}

	public boolean isRecordMouseClickOnly() {
		return recordMouseClickOnly;
	}

	public void setRecordMouseClickOnly(boolean recordMouseClickOnly) {
		//this.recordMouseClickOnly = recordMouseClickOnly;
	}

	public boolean isHaltTaskByEscape() {
		return haltTaskByEscape;
	}

	public void setHaltTaskByEscape(boolean haltTaskByEscape) {
		//this.haltTaskByEscape = haltTaskByEscape;
	}

	public boolean isExecuteOnRelease() {
		return executeOnRelease;
	}

	public void setExecuteOnRelease(boolean executeOnRelease) {
		//this.executeOnRelease = executeOnRelease;
	}

	public boolean isUseClipboardToTypeString() {
		return useClipboardToTypeString;
	}

	public void setUseClipboardToTypeString(boolean useClipboardToTypeString) {
		//this.useClipboardToTypeString = useClipboardToTypeString;
	}

	public boolean isRunTaskWithServerConfig() {
		return runTaskWithServerConfig;
	}

	public void setRunTaskWithServerConfig(boolean runTaskWithServerConfig) {
		//this.runTaskWithServerConfig = runTaskWithServerConfig;
	}

	public boolean isUseJavaAwtToGetMousePosition() {
		return useJavaAwtToGetMousePosition;
	}

	public void setUseJavaAwtToGetMousePosition(boolean useJavaAwtToGetMousePosition) {
		//this.useJavaAwtToGetMousePosition = useJavaAwtToGetMousePosition;
	}

	public boolean isUseTrayIcon() {
		return useTrayIcon;
	}

	public void setUseTrayIcon(boolean useTrayIcon) {
		//this.useTrayIcon = useTrayIcon;
	}

	public List<RenderedDebugLevel> getDebugLevels() {
		return debugLevels;
	}

	public void setDebugLevels(List<RenderedDebugLevel> debugLevels) {
		//this.debugLevels = debugLevels;
	}
}
