package core.webui.server.handlers.internals.taskactivation;

import argo.jdom.JsonNode;
import core.keyChain.MouseGesture;
import core.keyChain.TaskActivationConstructor;
import core.keyChain.TaskActivationConstructorManager;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.renderedobjects.RenderedMouseGestureActivation;
import core.webui.webcommon.HttpServerUtilities;

import utilities.json.JSONUtility;

import java.util.*;

public final class ActionTaskActivationSetMouseGesturesHandler extends AbstractTaskActivationConstructorActionHandler {

    public ActionTaskActivationSetMouseGesturesHandler(ObjectRenderer objectRenderer, TaskActivationConstructorManager taskActivationConstructorManager) {
        super(objectRenderer, taskActivationConstructorManager);
    }

    @Override
    public void handleRequestWithBackendAndConstructor(HttpExchange exchange, TaskActivationConstructor constructor, Map<String, String> params) {
        String nodeString = params.get("gestures");
        if (nodeString == null) {
            HttpServerUtilities.prepareHttpResponse(exchange, 400, "List of gesture indices must be provided."); 
return;
        }

        JsonNode node = JSONUtility.jsonFromString(nodeString);
        if (node == null) {
            HttpServerUtilities.prepareHttpResponse(exchange, 400, "Failed to parse list of gesture indices as JSON."); 
return;
        }
        if (!node.isArrayNode()) {
            HttpServerUtilities.prepareHttpResponse(exchange, 400, "List of gesture indices must be a list."); 
return;
        }
        List<Integer> indices = new ArrayList<>();
        for (JsonNode index : node.getNullableArrayNode()) {
            if (!index.isNumberValue()) {
                HttpServerUtilities.prepareHttpResponse(exchange, 400, "All gesture indices must be numbers."); 
return;
            }
            indices.add(Integer.parseInt(index.getNumberValue()));
        }

        MouseGesture[] gestures = RenderedMouseGestureActivation.INDICES;
        Set<MouseGesture> chosenGestures = new HashSet<>();
        for (int i : indices) {
            if (i < 0 || i >= gestures.length) {
                HttpServerUtilities.prepareHttpResponse(exchange, 400, "Gesture index out of bound: " + i + "."); 
return;
            }
            chosenGestures.add(gestures[i]);
        }

        constructor.setMouseGestures(chosenGestures);
        HttpServerUtilities.prepareHttpResponse(exchange, 200, "");
    }
}