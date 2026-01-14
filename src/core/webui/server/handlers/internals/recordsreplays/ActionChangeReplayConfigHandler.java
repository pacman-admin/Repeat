package core.webui.server.handlers.internals.recordsreplays;

import argo.jdom.JsonNode;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.webcommon.HttpServerUtilities;
import main.Backend;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import utilities.NumberUtility;
import utilities.json.JSONUtility;
import utilities.json.Jsonizer;

import java.util.Map;

public final class ActionChangeReplayConfigHandler extends AbstractSingleMethodHttpHandler {

    public ActionChangeReplayConfigHandler() {
        super(AbstractSingleMethodHttpHandler.POST_METHOD);
    }

    @Override
    protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange) {
        Map<String, String> params = HttpServerUtilities.parseSimplePostParameters(request);
        if (params == null) {
            return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Failed to get POST parameters.");
        }

        long count;
        long delay;
        float speedup;

        String countString = params.get("count");
        if (countString != null) {
            if (!NumberUtility.isPositiveInteger(countString)) {
                return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Count must be positive integer.");
            }
            count = Long.parseLong(countString);
        } else {
            count = Backend.replayConfig.getCount();
        }

        String delayString = params.get("delay");
        if (delayString != null) {
            if (!NumberUtility.isPositiveInteger(delayString)) {
                return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Delay must be non-negative integer.");
            }
            delay = Long.parseLong(delayString);
        } else {
            delay = Backend.replayConfig.getDelay();
        }

        String speedupString = params.get("speedup");
        if (speedupString != null) {
            if (!NumberUtility.isDouble(speedupString)) {
                return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Speedup must be a float number.");
            }
            speedup = Float.parseFloat(speedupString);
            if (speedup <= 0) {
                return HttpServerUtilities.prepareHttpResponse(exchange, 400, "Speedup must be a positive float number.");
            }
        } else {
            speedup = Backend.replayConfig.getSpeedup();
        }

        Backend.setReplayCount(count);
        Backend.setReplayDelay(delay);
        Backend.setReplaySpeedup(speedup);

        JsonNode responseNode = Jsonizer.jsonize(ResponseMessage.of(count, delay, speedup));
        if (responseNode == null) {
            return HttpServerUtilities.prepareHttpResponse(exchange, 500, "Failed to jsonize response.");
        }

        return HttpServerUtilities.prepareHttpResponse(exchange, 200, JSONUtility.jsonToString(responseNode.getRootNode()));
    }

    @SuppressWarnings("unused")
    private static final class ResponseMessage {
        private long count;
        private long delay;
        private float speedup;

        private static ResponseMessage of(long count, long delay, float speedup) {
            ResponseMessage output = new ResponseMessage();
            output.count = count;
            output.delay = delay;
            output.speedup = speedup;
            return output;
        }
    }
}
