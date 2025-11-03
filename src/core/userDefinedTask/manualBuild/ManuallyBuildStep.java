package core.userDefinedTask.manualBuild;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import core.controller.Core;
import core.userDefinedTask.manualBuild.steps.*;
import utilities.json.IJsonable;
import utilities.json.Jsonizer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;

public abstract class ManuallyBuildStep implements IJsonable {

    private static final Logger LOGGER = Logger.getLogger(ManuallyBuildStep.class.getName());
    private static final Map<String, Function<JsonNode, ManuallyBuildStep>> PARSERS;

    static {
        PARSERS = new HashMap<>();
        PARSERS.put(ControllerDelayStep.of(0).getJsonSignature(), ControllerDelayStep::parseJSON);
        PARSERS.put(KeyboardPressKeyStep.of(0).getJsonSignature(), KeyboardPressKeyStep::parseJSON);
        PARSERS.put(KeyboardReleaseKeyStep.of(0).getJsonSignature(), KeyboardReleaseKeyStep::parseJSON);
        PARSERS.put(KeyboardTypeKeyStep.of(0).getJsonSignature(), KeyboardTypeKeyStep::parseJSON);
        PARSERS.put(KeyboardTypeStringStep.of("").getJsonSignature(), KeyboardTypeStringStep::parseJSON);
        PARSERS.put(MouseClickCurrentPositionStep.of(0).getJsonSignature(), MouseClickCurrentPositionStep::parseJSON);
        PARSERS.put(MouseClickStep.of(0, 0, 0).getJsonSignature(), MouseClickStep::parseJSON);
        PARSERS.put(MouseMoveStep.of(0, 0).getJsonSignature(), MouseMoveStep::parseJSON);
        PARSERS.put(MouseMoveByStep.of(0, 0).getJsonSignature(), MouseMoveByStep::parseJSON);
        PARSERS.put(MousePressCurrentPositionStep.of(0).getJsonSignature(), MousePressCurrentPositionStep::parseJSON);
        PARSERS.put(MouseReleaseCurrentPositionStep.of(0).getJsonSignature(), MouseReleaseCurrentPositionStep::parseJSON);
    }

    public static ManuallyBuildStep parseJSON(JsonNode data) {
        String signature = data.getStringValue("signature");
        Function<JsonNode, ManuallyBuildStep> parser = PARSERS.get(signature);
        if (parser == null) {
            LOGGER.warning("Unable to find parser for signature " + signature + ".");
            return null;
        }

        return parser.apply(data.getNode("data"));
    }

    public abstract void execute(Core controller) throws InterruptedException;

    public abstract String getDisplayString();

    protected abstract String getJsonSignature();

    protected void parse(JsonNode node) {
        Jsonizer.parse(node, this);
    }

    private JsonNode jsonizeContent() {
        return Jsonizer.jsonize(this);
    }

    @Override
    public final JsonRootNode jsonize() {
        return JsonNodeFactories.object(
                JsonNodeFactories.field("signature", JsonNodeFactories.string(getJsonSignature())),
                JsonNodeFactories.field("data", jsonizeContent()));
    }
}
