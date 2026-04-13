package core.keyChain;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import utilities.json.IJsonable;
import utilities.json.JSONUtility;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Represents an entity that activates a {@link core.userDefinedTask.UserDefinedAction}.
 */
@SuppressWarnings("unused")
public final class ActionInvoker implements IJsonable {

    private static final Logger LOGGER = Logger.getLogger(ActionInvoker.class.getName());
    private Set<KeyChain> hotkeys;
    private Set<MouseGesture> mouseGestures;
    private Set<KeySequence> keySequences;

    private ActionInvoker(Builder builder) {
        hotkeys = builder.hotkeys;
        mouseGestures = builder.mouseGestures;
        keySequences = builder.keySequences;
    }

    /**
     * Construct a new object from a json node.
     *
     * @param node json node to parse
     * @return the new object with content parsed, or null if cannot parse.
     */
    public static ActionInvoker parseJSON(JsonNode node) {
        try {
            List<JsonNode> hotkeysNode = node.getArrayNode("hotkey");
            List<JsonNode> keySequenceNodes = node.isArrayNode("key_sequence") ? node.getArrayNode("key_sequence") : new ArrayList<>();
            List<JsonNode> mouseGestureNode = node.getArrayNode("mouse_gesture");

            Set<KeyChain> keyChains = new HashSet<>();
            for (JsonNode hotkeyNode : hotkeysNode) {
                KeyChain newKeyChain = KeyChain.parseJSON(hotkeyNode.getArrayNode());
                if (newKeyChain == null) {
                    LOGGER.log(Level.WARNING, "Cannot parse key chain " + hotkeyNode);
                } else {
                    keyChains.add(newKeyChain);
                }
            }

            Set<KeySequence> keySequences = new HashSet<>();
            for (JsonNode keySequenceNode : keySequenceNodes) {
                KeySequence newkeySequence = KeySequence.parseJSON(keySequenceNode.getArrayNode());
                if (newkeySequence == null) {
                    LOGGER.log(Level.WARNING, "Cannot parse key chain " + keySequenceNode);
                } else {
                    keySequences.add(newkeySequence);
                }
            }

            Set<MouseGesture> gestures = MouseGesture.parseJSON(mouseGestureNode);

            return ActionInvoker.newBuilder()
                    .withHotKeys(keyChains)
                    .withKeySequence(keySequences)
                    .withMouseGestures(gestures)
                    .build();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Exception while parsing task activation.", e);
            return null;
        }
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    /**
     * @param hotkeys the hotkey set to set
     */
    private void setHotKeys(Set<KeyChain> hotkeys) {
        this.hotkeys = new HashSet<>();
        this.hotkeys.addAll(hotkeys);
    }

    /**
     * @return the set of key chains associated with this activation entity.
     */
    public Set<KeyChain> getHotkeys() {
        if (hotkeys == null) {
            hotkeys = new HashSet<>();
        }
        return hotkeys;
    }

    /**
     * @return an arbitrary {@link KeyChain} from the set of keychains, or null if the set is empty.
     */
    public KeyChain getFirstHotkey() {
        Set<KeyChain> hotkeys = getHotkeys();
        if (hotkeys.isEmpty()) {
            return null;
        } else {
            return hotkeys.iterator().next();
        }
    }

    /**
     * @return set of mouse gestures associated with this activation entity.
     */
    public Set<MouseGesture> getMouseGestures() {
        if (mouseGestures == null) {
            mouseGestures = new HashSet<>();
        }

        return mouseGestures;
    }

    /**
     * @param mouseGestures set of mouse gestures to set.
     */
    private void setMouseGestures(Set<MouseGesture> mouseGestures) {
        this.mouseGestures = new HashSet<>();
        this.mouseGestures.addAll(mouseGestures);
    }

    /**
     * @return an arbitrary {@link MouseGesture} from the set of gestures, or null if the set is empty.
     */
    public MouseGesture getFirstMouseGesture() {
        Set<MouseGesture> gestures = getMouseGestures();
        if (gestures.isEmpty()) {
            return null;
        } else {
            return gestures.iterator().next();
        }
    }

    /**
     * @return set of key sequences associated with this activation entity.
     */
    public Set<KeySequence> getKeySequences() {
        if (keySequences == null) {
            return new HashSet<>();
        }

        return keySequences;
    }

    /**
     * @param keySequences set of key sequences to set.
     */
    private void setKeySequences(Set<KeySequence> keySequences) {
        this.keySequences = new HashSet<>();
        this.keySequences.addAll(keySequences);
    }

    /**
     * @return an arbitrary {@link KeySequence} from the set of gestures, or null if the set is empty.
     */
    public KeySequence getFirstKeySequence() {
        Set<KeySequence> keySequences = getKeySequences();
        if (keySequences.isEmpty()) {
            return null;
        } else {
            return keySequences.iterator().next();
        }
    }

    /**
     * Copy the content of the other {@link ActionInvoker} to this object.
     *
     * @param other other task activation whose content will be copied from.
     */
    public void copy(ActionInvoker other) {
        setHotKeys(other.getHotkeys());
        setMouseGestures(other.getMouseGestures());
        setKeySequences(other.getKeySequences());
    }

    /**
     * Check if this activation is empty (i.e. no event for activation).
     */
    public boolean isEmpty() {
        return getHotkeys().isEmpty() && getMouseGestures().isEmpty() && getKeySequences().isEmpty();
    }

    /**
     * Returns a representative string for this activation.
     * Iterating through all types of activations and select one entry at random.
     */
    public String getRepresentativeString() {
        if (!getHotkeys().isEmpty()) {
            return "{" + getHotkeys().iterator().next().toString() + "}";
        }
        if (!getKeySequences().isEmpty()) {
            return "<" + getKeySequences().iterator().next().toString() + ">";
        }
        if (!getMouseGestures().isEmpty()) {
            return "[" + getMouseGestures().iterator().next().toString() + "]";
        }
        return new KeyChain().toString();
    }

    @Override
    public JsonRootNode jsonize() {
        return JsonNodeFactories.object(
                JsonNodeFactories.field("hotkey", JsonNodeFactories.array(JSONUtility.listToJson(getHotkeys()))),
                JsonNodeFactories.field("key_sequence", JsonNodeFactories.array(JSONUtility.listToJson(getKeySequences()))),
                JsonNodeFactories.field("mouse_gesture", JsonNodeFactories.array(JSONUtility.listToJson(getMouseGestures()))));
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                getHotkeys(),
                getKeySequences(),
                getMouseGestures());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ActionInvoker other = (ActionInvoker) obj;
        return getHotkeys().equals(other.getHotkeys())
                && getKeySequences().equals(other.getKeySequences())
                && getMouseGestures().equals(other.getMouseGestures());
    }

    /**
     * Builder for enclosing class.
     */
    public static final class Builder {
        private final Set<KeyChain> hotkeys;
        private final Set<MouseGesture> mouseGestures;
        private final Set<KeySequence> keySequences;

        private Builder() {
            hotkeys = new HashSet<>();
            mouseGestures = new HashSet<>();
            keySequences = new HashSet<>();
        }

        public Builder addHotKeys(KeyChain... keys) {
            hotkeys.addAll(Arrays.asList(keys));
            return this;
        }

        public Builder withHotKey(KeyChain key) {
            hotkeys.clear();
            hotkeys.add(key);
            return this;
        }

        public Builder withHotKeys(Collection<KeyChain> keys) {
            this.hotkeys.clear();
            this.hotkeys.addAll(keys);
            return this;
        }

        public Builder addMouseGesture(MouseGesture... gestures) {
            mouseGestures.addAll(Arrays.asList(gestures));
            return this;
        }

        public Builder withMouseGesture(MouseGesture gesture) {
            mouseGestures.clear();
            mouseGestures.add(gesture);
            return this;
        }

        public Builder withMouseGestures(Collection<MouseGesture> gestures) {
            this.mouseGestures.clear();
            this.mouseGestures.addAll(gestures);
            return this;
        }

        public Builder addKeySequence(KeySequence... keySequences) {
            this.keySequences.addAll(Arrays.asList(keySequences));
            return this;
        }

        public Builder withKeySequence(KeySequence keySequences) {
            this.keySequences.clear();
            this.keySequences.add(keySequences);
            return this;
        }

        public Builder withKeySequence(Collection<KeySequence> keySequences) {
            this.keySequences.clear();
            this.keySequences.addAll(keySequences);
            return this;
        }

        public ActionInvoker build() {
            return new ActionInvoker(this);
        }
    }
}
