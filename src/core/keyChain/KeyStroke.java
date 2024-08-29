package core.keyChain;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import org.simplenativehooks.events.NativeKeyEvent;
import utilities.KeyCodeToChar;
import utilities.KeyEventCodeToString;

import java.time.LocalDateTime;
import java.util.logging.Logger;

/**
 * Represents a keystroke on the keyboard.
 */
@SuppressWarnings("unused")
public final class KeyStroke implements ButtonStroke {

    private static final Logger LOGGER = Logger.getLogger(KeyStroke.class.getName());

    private static final String TYPE_STRING = "key_stroke";
    private final int key;
    private final Modifier modifier;
    private boolean pressed; // Press or release.
    private LocalDateTime invokedTime;

    private KeyStroke(int key, Modifier modifier, boolean press, LocalDateTime invokedTime) {
        this.key = key;
        this.modifier = modifier;
        this.pressed = press;
        this.invokedTime = invokedTime;
    }

    public static KeyStroke of(int key, Modifier modifier) {
        return new KeyStroke(key, modifier, false, LocalDateTime.now());
    }

    private static KeyStroke of(int key, Modifier modifier, boolean press, LocalDateTime invokedTime) {
        return new KeyStroke(key, modifier, press, invokedTime);
    }

    public static KeyStroke of(NativeKeyEvent e) {
        Modifier m = switch (e.getModifier()) {
            case KEY_MODIFIER_LEFT -> Modifier.KEY_MODIFIER_LEFT;
            case KEY_MODIFIER_RIGHT -> Modifier.KEY_MODIFIER_RIGHT;
            default -> Modifier.KEY_MODIFIER_UNKNOWN;
        };

        return of(e.getKey(), m, e.isPressed(), e.getInvokedTime());
    }

    public static KeyStroke parseJSON(JsonNode n) {
        if (n.isNumberValue()) {
            return of(Integer.parseInt(n.getNumberValue()), Modifier.KEY_MODIFIER_UNKNOWN);
        }

        int key = Integer.parseInt(n.getNumberValue("key"));
        int modifier = Integer.parseInt(n.getNumberValue("modifier"));
        return of(key, Modifier.forValue(modifier));
    }

    public NativeKeyEvent toNativeKeyEvent() {
        return NativeKeyEvent.of(key, modifier.toNativeModifier(), pressed);
    }

    /**
     * Retrieve the keyon the keyboard. This alone does not identify the exact key
     * for cases like Ctrl and Shift, which have left and right keys.
     *
     * @return the integer representing the keyon the keyboard.
     */
    @Override
    public int getKey() {
        return key;
    }

    @Override
    public Source getSource() {
        return Source.KEYBOARD;
    }

    /**
     * Retrieve the modifier of the keystroke. Either left or right for
     * keys like shift, ctrl or alt, or has undefined meaning for keys
     * that have only 1 keyon the keyboard (virtually all others).
     *
     * @return the modifier of the keystroke.
     */
    private Modifier getModifier() {
        return modifier;
    }

    /**
     * Syntactic sugar for {@link #getModifier()}.
     */
    public Modifier m() {
        return modifier;
    }

    public KeyStroke at(LocalDateTime invokedTime) {
        this.invokedTime = invokedTime;
        return this;
    }

    public KeyStroke press(boolean pressed) {
        this.pressed = pressed;
        return this;
    }

    @Override
    public boolean isPressed() {
        return pressed;
    }

    @Override
    public KeyStroke clone() {
        return of(key, modifier, pressed, invokedTime);
    }

    @Override
    public String toString() {
        String suffix = modifier == Modifier.KEY_MODIFIER_LEFT ? " (L)" : "";
        if (modifier == Modifier.KEY_MODIFIER_RIGHT) {
            suffix = " (R)";
        }
        return KeyEventCodeToString.codeToString(key) + suffix;
    }

    @Override
    public KeyboardResult getTypedString(KeyboardState keyboardState) {
        keyboardState = keyboardState.changeWith(this);
        String s = KeyCodeToChar.getCharForCode(key, keyboardState);

        return KeyboardResult.of(keyboardState, s);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + key;
        result = prime * result + ((modifier == null) ? 0 : modifier.getHashCode());
        return result;
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
        KeyStroke other = (KeyStroke) obj;
        if (key != other.key) {
            return false;
        }
        return modifier.equivalent(other.modifier);
    }

    @Override
    public JsonRootNode jsonize() {
        return JsonNodeFactories.object(JsonNodeFactories.field("type", JsonNodeFactories.string(TYPE_STRING)), JsonNodeFactories.field("key", JsonNodeFactories.number(key)), JsonNodeFactories.field("modifier", JsonNodeFactories.number(modifier.getValue())));
    }

    public enum Modifier {
        KEY_MODIFIER_UNKNOWN(0), // Unknown is equal to both left and right.
        KEY_MODIFIER_LEFT(1), KEY_MODIFIER_RIGHT(2);

        private final int value;

        Modifier(int value) {
            this.value = value;
        }

        static Modifier forValue(int value) {
            for (Modifier m : Modifier.values()) {
                if (m.value == value) {
                    return m;
                }
            }

            LOGGER.warning("Unknown keystroke modifier for value " + value + ".");
            return KEY_MODIFIER_UNKNOWN;
        }

        public int getValue() {
            return this.value;
        }

        private int getHashCode() {
            return value;
        }

        private boolean equivalent(Modifier other) {
            return (this == KEY_MODIFIER_UNKNOWN) || (other == KEY_MODIFIER_UNKNOWN) || (this == other);
        }

        org.simplenativehooks.events.NativeKeyEvent.Modifier toNativeModifier() {
            return switch (this) {
                case KEY_MODIFIER_UNKNOWN -> NativeKeyEvent.Modifier.KEY_MODIFIER_UNKNOWN;
                case KEY_MODIFIER_LEFT -> NativeKeyEvent.Modifier.KEY_MODIFIER_LEFT;
                case KEY_MODIFIER_RIGHT -> NativeKeyEvent.Modifier.KEY_MODIFIER_RIGHT;
            };
        }
    }
}
