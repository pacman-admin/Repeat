package core.webui.server.handlers.internals.tasks.manuallybuild;

import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.internals.tasks.manuallybuild.ManuallyBuildActionFeModel.Actor;
import core.webui.server.handlers.internals.tasks.manuallybuild.ManuallyBuildActionFeModel.ControllerAction;
import core.webui.server.handlers.internals.tasks.manuallybuild.ManuallyBuildActionFeModel.KeyboardAction;
import core.webui.server.handlers.internals.tasks.manuallybuild.ManuallyBuildActionFeModel.MouseAction;
import core.webui.webcommon.HttpServerUtilities;

import java.util.Map;

public final class ActionManuallyBuildActionParametersPlaceHolderHandler extends AbstractSingleMethodHttpHandler {

    public ActionManuallyBuildActionParametersPlaceHolderHandler() {
        super(AbstractSingleMethodHttpHandler.GET_METHOD);
    }

    @Override
    public void handleAllowedRequestWithBackend(HttpExchange exchange) {
        Map<String, String> parameters = HttpServerUtilities.parseGetParameters(exchange.getRequestURI());
        if (parameters == null) {
            HttpServerUtilities.prepareTextResponse(exchange, 400, "Unable to parse GET parameters.");
            return;
        }
        String actor = parameters.get("actor");
        if (actor == null) {
            HttpServerUtilities.prepareTextResponse(exchange, 400, "Actor must be provided.");
            return;
        }
        String action = parameters.get("action");
        if (action == null) {
            HttpServerUtilities.prepareTextResponse(exchange, 400, "Action must be provided.");
            return;
        }
        actor = actor.toLowerCase();
        action = action.toLowerCase();

        try {
            String placeholder = placeHolderParametersText(actor, action);
            HttpServerUtilities.prepareHttpResponse(exchange, 200, placeholder);
        } catch (InvalidManuallyBuildComponentException e) {
            HttpServerUtilities.prepareHttpResponse(exchange, 400, e.getMessage());
        }
    }

    private String placeHolderParametersText(String actor, String action) throws InvalidManuallyBuildComponentException {
        if (actor.equals(Actor.MOUSE.toString())) {
            String maskPlaceholder = "<mask (left/middle/right)>";

            if (action.equals(MouseAction.CLICK.toString())) {
                return maskPlaceholder + ",<x coordinate>,<y coordinate>";
            } else if (action.equals(MouseAction.CLICK_CURRENT_POSITION.toString())) {
                return maskPlaceholder;
            } else if (action.equals(MouseAction.MOVE_BY.toString())) {
                return "<x pixels>,<y pixels>";
            } else if (action.equals(MouseAction.MOVE.toString())) {
                return "<x coordinate>,<y coordinate>";
            } else if (action.equals(MouseAction.PRESS_CURRENT_POSITION.toString())) {
                return maskPlaceholder;
            } else if (action.equals(MouseAction.RELEASE_CURRENT_POSITION.toString())) {
                return maskPlaceholder;
            } else {
                throw new InvalidManuallyBuildComponentException("Unknown action " + action + " for actor " + actor + ".");
            }
        } else if (actor.equals(Actor.KEYBOARD.toString())) {
            String keyPlaceholder = "<key (A/B/C/DELETE/1/....)>";
            String multiKeysPlaceholder = keyPlaceholder + ",<key2>,<key3>,...";

            if (action.equals(KeyboardAction.PRESS_KEY.toString())) {
                return multiKeysPlaceholder;
            } else if (action.equals(KeyboardAction.RELEASE_KEY.toString())) {
                return multiKeysPlaceholder;
            } else if (action.equals(KeyboardAction.TYPE_KEY.toString())) {
                return multiKeysPlaceholder;
            } else if (action.equals(KeyboardAction.TYPE_STRING_KEY.toString())) {
                return "<any text string>";
            } else {
                throw new InvalidManuallyBuildComponentException("Unknown action " + action + " for actor " + actor + ".");
            }
        } else if (actor.equals(Actor.CONTROLLER.toString())) {
            if (action.equals(ControllerAction.WAIT.toString())) {
                return "<wait time in milliseconds>";
            } else {
                throw new InvalidManuallyBuildComponentException("Unknown action " + action + " for actor " + actor + ".");
            }
        } else {
            throw new InvalidManuallyBuildComponentException("Unknown actor " + actor + ".");
        }
    }
}
