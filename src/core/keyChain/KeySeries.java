package core.keyChain;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import utilities.Function;
import utilities.KeyCodeToChar;
import utilities.StringUtilities;
import utilities.json.IJsonable;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public abstract class KeySeries implements IJsonable {

    private static final Logger LOGGER = Logger.getLogger(KeySeries.class.getName());
    List<ButtonStroke> keys;

    KeySeries(List<ButtonStroke> keys) {
        this.keys = new ArrayList<>(keys.size());

        this.keys.addAll(keys);
    }

    KeySeries(Iterable<Integer> keys) {
        this.keys = new ArrayList<>();

        for (Integer key : keys) {
            this.keys.add(KeyStroke.of(key, KeyStroke.Modifier.KEY_MODIFIER_UNKNOWN));
        }
    }

    KeySeries(int key) {
        this(List.of(key));
    }

    KeySeries() {
        this(new ArrayList<Integer>());
    }

    static List<ButtonStroke> parseKeyStrokes(List<JsonNode> list) {
        try {
            return list.stream().map(ButtonStroke::parseJSON).collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Unable to parse key series", e);
            return null;
        }
    }

    public abstract boolean collideWith(KeySeries other);

    /**
     * @return list of keycodes in this keychain.
     * @deprecated change to use {@link #getButtonStrokes()} instead.
     */
    @Deprecated
    public List<Integer> getKeys() {
        List<Integer> output = new ArrayList<>();
        for (ButtonStroke key : keys) {
            output.add(key.getKey());
        }
        return output;
    }

    /**
     * @return the list of keystrokes contained in this keychain.
     * @deprecated change to use {@link #getButtonStrokes()} instead. This will not
     * include any mouse related strokes.
     */
    @Deprecated
    public List<KeyStroke> getKeyStrokes() {
        List<KeyStroke> output = new ArrayList<>(keys.size());
        for (ButtonStroke key : keys) {
            ButtonStroke stroke = key.clone();
            if (stroke instanceof KeyStroke) {
                output.add((KeyStroke) stroke);
            }
        }
        return output;
    }

    /**
     * @return the list of button strokes contained in this keychain.
     */
    public final List<ButtonStroke> getButtonStrokes() {
        List<ButtonStroke> output = new ArrayList<>(keys.size());
        for (ButtonStroke key : keys) {
            output.add(key.clone());
        }
        return output;
    }

    /**
     * @return the number of keystrokes in this keychain.
     */
    public final int getSize() {
        return keys.size();
    }

    /*
     * Add all keystrokes from another keychain.
     */
    public void addFrom(KeySeries other) {
        keys.addAll(other.keys);
    }

    /**
     * Add a single stroke to the keychain.
     *
     * @param stroke stroke to add.
     */
    public void addKeyStroke(ButtonStroke stroke) {
        keys.add(stroke);
    }

    /**
     * Remove all keys in this keychain.
     */
    public final void clearKeys() {
        keys.clear();
    }

    /**
     * Check whether this keychain contains no key.
     *
     * @return if there is no keystroke in this keychain.
     */
    public boolean isEmpty() {
        return keys.isEmpty();
    }

    /**
     * @param stroke the keystroke to find.
     * @return whether the given keystroke is in this keychain.
     */
    public boolean contains(ButtonStroke stroke) {
        for (ButtonStroke key : keys) {
            if (key.equals(stroke)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the string which would be typed out if all keys in this {@link KeySequence} are pressed in the specified order.
     * Note that this ignores effects of keys like SHIFT, CAPSLOCK, or NUMLOCK.
     */
    public String getTypedString() {
        StringBuilder builder = new StringBuilder();
        KeyboardState keyboardState = KeyboardState.getDefault();

        for (ButtonStroke keyStroke : getButtonStrokes()) {
            String s = KeyCodeToChar.getCharForCode(keyStroke.getKey(), keyboardState);
            builder.append(s);
        }

        return builder.toString();
    }

    @Override
    public String toString() {
        return StringUtilities.join(keys.stream().map(Object::toString).collect(Collectors.toList()), " + ");
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
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
        KeySeries other = (KeySeries) obj;
        if (keys == null) {
            return other.keys == null;
        } else {
            return this.keys.equals(other.keys);
        }
    }

    @Override
    public JsonRootNode jsonize() {
        List<JsonNode> keyChain = new Function<ButtonStroke, JsonNode>() {
            @Override
            public JsonNode apply(ButtonStroke s) {
                return s.jsonize();
            }
        }.map(getButtonStrokes());

        return JsonNodeFactories.array(keyChain);
    }
}
