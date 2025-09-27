package core.keyChain;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import utilities.json.IJsonable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Enum representing classification categories
 */
public enum MouseGesture implements IJsonable {
    ALPHA("alpha"), CIRCLE_LEFT("circle_left"), GAMMA("gamma"), GREATER_THAN("greater_than"), HAT("hat"), HORIZONTAL("horizontal"), LESS_THAN("less_than"), N("N"), RANDOM("random"), SIX("six"), SQUARE("square"), SQUARE_ROOT("square_root"), TILDA("tilda"), TRIANGLE("triangle"), U("u"), VERTICAL("vertical"), Z("z");

    public static final Set<MouseGesture> IGNORED_CLASSIFICATIONS = new HashSet<>(Arrays.asList(HORIZONTAL, VERTICAL, SIX, U, RANDOM));

    private final String text;

    /**
     * @param text human readable text form of this classification
     */
    MouseGesture(final String text) {
        this.text = text;
    }

    /**
     * @return list of enabled mouse gestures that can be used to activate tasks.
     */
    public static List<MouseGesture> enabledGestures() {
        return List.of(values());
    }

    /**
     * Find the mouse gesture given its name.
     *
     * @param name name of the mouse gesture
     * @return the found mouse gesture, or null if cannot find one
     */
    public static MouseGesture find(String name) {
        for (MouseGesture classification : MouseGesture.values()) {
            if (classification.text.equals(name)) {
                return classification;
            }
        }

        return null;
    }

    /**
     * Parse a json list of strings into a set of mouse gestures.
     *
     * @param nodes the json list of strings
     * @return set of mouse gestures parsed.
     */
    public static Set<MouseGesture> parseJSON(List<JsonNode> nodes) {
        Set<MouseGesture> output = new HashSet<>();
        for (JsonNode node : nodes) {
            String name = node.getStringValue("name");
            MouseGesture gesture = find(name);

            if (gesture != null) {
                output.add(gesture);
            }
        }
        return output;
    }

    @Override
    public String toString() {
        return text;
    }

    @Override
    public JsonRootNode jsonize() {
        return JsonNodeFactories.object(JsonNodeFactories.field("name", JsonNodeFactories.string(text)));
    }
}