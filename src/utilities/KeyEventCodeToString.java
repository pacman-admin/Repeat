package utilities;

import java.awt.event.KeyEvent;

public class KeyEventCodeToString {

    private KeyEventCodeToString() {
        throw new InstantiationError("This class is uninstantiable.");
    }

    /**
     * Convert from code from KeyEvent.VK_* to a human friendly string.
     * This is needed because in some OSes the KeyEvent utility does
     * not work properly.
     */
    public static String codeToString(int code) {
        return switch (code) {
            case KeyEvent.VK_CONTROL -> "Ctrl";
            case KeyEvent.VK_ALT -> {
                if (OSIdentifier.isMac()) {
                    yield "option";
                }
                yield "Alt";
            }
            case KeyEvent.VK_WINDOWS -> "Windows";
            case KeyEvent.VK_META -> {
                if (OSIdentifier.isMac()) {
                    yield "command";
                }
                yield "Meta";
            }
            case KeyEvent.VK_SHIFT -> "Shift";
            case KeyEvent.VK_TAB -> "Tab";
            case KeyEvent.VK_SPACE -> "Space";
            default -> KeyEvent.getKeyText(code);
        };
    }
}