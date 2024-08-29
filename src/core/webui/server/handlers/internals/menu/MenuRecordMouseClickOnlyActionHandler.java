package core.webui.server.handlers.internals.menu;

import java.io.IOException;

import org.apache.http.nio.protocol.HttpAsyncExchange;

import core.recorder.Recorder;
import core.webui.server.handlers.AbstractBooleanConfigHttpHandler;
import core.webui.webcommon.HttpServerUtilities;

public final class MenuRecordMouseClickOnlyActionHandler extends AbstractBooleanConfigHttpHandler {

	@Override
	protected Void handleAllowedRequestWithBackendAndValue(HttpAsyncExchange exchange, boolean value) throws IOException {
        backEndHolder.getRecorder().setRecordMode(value ? Recorder.MODE_MOUSE_CLICK_ONLY : Recorder.MODE_NORMAL);
		return HttpServerUtilities.prepareHttpResponse(exchange, 200, "");
	}
}
