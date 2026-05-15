package core.webui.server.handlers.renderedobjects;

import core.config.Config;
import core.recorder.Recorder;

import java.util.List;

public final class RenderedConfig {
    private final boolean recordMouseClickOnly;
    private final boolean executeOnRelease;
    private final boolean useClipboardToTypeString;
    private final boolean useJavaAwtToGetMousePosition;
    private final List<RenderedDebugLevel> debugLevels;

    public RenderedConfig(Config config, Recorder recorder) {
        executeOnRelease = config.isExecuteOnKeyReleased();
        useClipboardToTypeString = config.isUseClipboardToTypeString();
        recordMouseClickOnly = recorder.getRecordMode() == Recorder.MODE_MOUSE_CLICK_ONLY;
        useJavaAwtToGetMousePosition = config.isUseJavaAwtToGetMousePosition();
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

    public boolean isUseJavaAwtToGetMousePosition() {
        return useJavaAwtToGetMousePosition;
    }

    public List<RenderedDebugLevel> getDebugLevels() {
        return debugLevels;
    }

}
