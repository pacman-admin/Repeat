package core.userDefinedTask.manualBuild.steps;

import java.awt.event.InputEvent;

class DisplayTextUtil {

    private DisplayTextUtil() {
        //This class is uninstantiable.
    }

    public static String coordinate(int x, int y) {
        return String.format("(%d, %d)", x, y);
    }

    public static String mouseMaskToString(int mask) {
        return switch (mask) {
            case InputEvent.BUTTON1_DOWN_MASK -> "left button";
            case InputEvent.BUTTON3_DOWN_MASK -> "right button";
            case InputEvent.BUTTON2_DOWN_MASK -> "middle";
            default -> "unknown button";
        };
    }
}
