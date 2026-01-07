package utilities;

import core.keyChain.KeyboardState;

import java.awt.event.KeyEvent;

public class KeyCodeToChar {

    private KeyCodeToChar() {
        throw new InstantiationError("This class is uninstantiable.");
    }

    public static boolean hasCharForCode(int code, KeyboardState state) {
        return !getCharForCode(code, state).isBlank();
    }

    public static String getCharForCode(int code, KeyboardState state) {
        String nonAlphaChar = getNonAlphaChar(code, state);
        String alphaChar = getAlphaChar(code, state);
        String numpadChar = getCharFromNumpadCode(code, state);

        boolean isNonAlpha = !nonAlphaChar.isBlank();
        boolean isAlpha = !alphaChar.isBlank();
        boolean isNumpad = !numpadChar.isBlank();

        int trueCount = (isNonAlpha ? 1 : 0) + (isAlpha ? 1 : 0) + (isNumpad ? 1 : 0);
        if (trueCount > 1) {
            throw new IllegalArgumentException("Code " + code + " is ambiguous.");
        }

        if (isNonAlpha) {
            return nonAlphaChar;
        }
        if (isAlpha) {
            return alphaChar;
        }
        if (isNumpad) {
            return numpadChar;
        }
        return "";
    }

    private static String getNonAlphaChar(int code, KeyboardState state) {
        if (state.isShiftLocked()) {
            return getNonAlphaCharWithShift(code);
        }
        return getNonAlphaCharWithoutShift(code);
    }

    private static String getAlphaChar(int code, KeyboardState state) {
        boolean capitalized = state.isCapslockLocked() ^ state.isShiftLocked();
        if (capitalized) {
            return getUpperCaseAlphaChar(code);
        }
        return getLowerCaseAlphaChar(code);
    }

    private static String getNonAlphaCharWithoutShift(int code) {
        return switch (code) {
            case KeyEvent.VK_BACK_QUOTE -> "`";
            case KeyEvent.VK_1 -> "1";
            case KeyEvent.VK_2 -> "2";
            case KeyEvent.VK_3 -> "3";
            case KeyEvent.VK_4 -> "4";
            case KeyEvent.VK_5 -> "5";
            case KeyEvent.VK_6 -> "6";
            case KeyEvent.VK_7 -> "7";
            case KeyEvent.VK_8 -> "8";
            case KeyEvent.VK_9 -> "9";
            case KeyEvent.VK_0 -> "0";
            case KeyEvent.VK_MINUS -> "-";
            case KeyEvent.VK_EQUALS -> "=";
            case KeyEvent.VK_OPEN_BRACKET -> "[";
            case KeyEvent.VK_CLOSE_BRACKET -> "]";
            case KeyEvent.VK_SEMICOLON -> ";";
            case KeyEvent.VK_QUOTE -> "'";
            case KeyEvent.VK_BACK_SLASH -> "\\";
            case KeyEvent.VK_COMMA -> ",";
            case KeyEvent.VK_PERIOD -> ".";
            case KeyEvent.VK_SLASH -> "/";
            case KeyEvent.VK_TAB -> "\t";
            case KeyEvent.VK_ENTER -> "\n";
            case KeyEvent.VK_SPACE -> " ";
            default -> "";
        };
    }

    private static String getNonAlphaCharWithShift(int code) {
        return switch (code) {
            case KeyEvent.VK_BACK_QUOTE -> "~";
            case KeyEvent.VK_1 -> "!";
            case KeyEvent.VK_2 -> "@";
            case KeyEvent.VK_3 -> "#";
            case KeyEvent.VK_4 -> "$";
            case KeyEvent.VK_5 -> "%";
            case KeyEvent.VK_6 -> "^";
            case KeyEvent.VK_7 -> "&";
            case KeyEvent.VK_8 -> "*";
            case KeyEvent.VK_9 -> "(";
            case KeyEvent.VK_0 -> ")";
            case KeyEvent.VK_MINUS -> "_";
            case KeyEvent.VK_EQUALS -> "+";
            case KeyEvent.VK_OPEN_BRACKET -> "{";
            case KeyEvent.VK_CLOSE_BRACKET -> "}";
            case KeyEvent.VK_SEMICOLON -> ":";
            case KeyEvent.VK_QUOTE -> "\"";
            case KeyEvent.VK_BACK_SLASH -> "|";
            case KeyEvent.VK_COMMA -> "<";
            case KeyEvent.VK_PERIOD -> ">";
            case KeyEvent.VK_SLASH -> "?";
            case KeyEvent.VK_TAB -> "\t";
            case KeyEvent.VK_ENTER -> "\n";
            case KeyEvent.VK_SPACE -> " ";
            default -> "";
        };
    }

    private static String getLowerCaseAlphaChar(int code) {
        return switch (code) {
            case KeyEvent.VK_Q -> "q";
            case KeyEvent.VK_W -> "w";
            case KeyEvent.VK_E -> "e";
            case KeyEvent.VK_R -> "r";
            case KeyEvent.VK_T -> "t";
            case KeyEvent.VK_Y -> "y";
            case KeyEvent.VK_U -> "u";
            case KeyEvent.VK_I -> "i";
            case KeyEvent.VK_O -> "o";
            case KeyEvent.VK_P -> "p";
            case KeyEvent.VK_A -> "a";
            case KeyEvent.VK_S -> "s";
            case KeyEvent.VK_D -> "d";
            case KeyEvent.VK_F -> "f";
            case KeyEvent.VK_G -> "g";
            case KeyEvent.VK_H -> "h";
            case KeyEvent.VK_J -> "j";
            case KeyEvent.VK_K -> "k";
            case KeyEvent.VK_L -> "l";
            case KeyEvent.VK_Z -> "z";
            case KeyEvent.VK_X -> "x";
            case KeyEvent.VK_C -> "c";
            case KeyEvent.VK_V -> "v";
            case KeyEvent.VK_B -> "b";
            case KeyEvent.VK_N -> "n";
            case KeyEvent.VK_M -> "m";
            default -> "";
        };
    }

    private static String getUpperCaseAlphaChar(int code) {
        return switch (code) {
            case KeyEvent.VK_Q -> "Q";
            case KeyEvent.VK_W -> "W";
            case KeyEvent.VK_E -> "E";
            case KeyEvent.VK_R -> "R";
            case KeyEvent.VK_T -> "T";
            case KeyEvent.VK_Y -> "Y";
            case KeyEvent.VK_U -> "U";
            case KeyEvent.VK_I -> "I";
            case KeyEvent.VK_O -> "O";
            case KeyEvent.VK_P -> "P";
            case KeyEvent.VK_A -> "A";
            case KeyEvent.VK_S -> "S";
            case KeyEvent.VK_D -> "D";
            case KeyEvent.VK_F -> "F";
            case KeyEvent.VK_G -> "G";
            case KeyEvent.VK_H -> "H";
            case KeyEvent.VK_J -> "J";
            case KeyEvent.VK_K -> "K";
            case KeyEvent.VK_L -> "L";
            case KeyEvent.VK_Z -> "Z";
            case KeyEvent.VK_X -> "X";
            case KeyEvent.VK_C -> "C";
            case KeyEvent.VK_V -> "V";
            case KeyEvent.VK_B -> "B";
            case KeyEvent.VK_N -> "N";
            case KeyEvent.VK_M -> "M";
            default -> "";
        };
    }

    private static String getCharFromNumpadCode(int code, KeyboardState state) {
        if (!state.isNumslockLocked() && !OSIdentifier.isMac()) {
            return "";
        }

        return switch (code) {
            case KeyEvent.VK_NUMPAD0 -> "0";
            case KeyEvent.VK_NUMPAD1 -> "1";
            case KeyEvent.VK_NUMPAD2 -> "2";
            case KeyEvent.VK_NUMPAD3 -> "3";
            case KeyEvent.VK_NUMPAD4 -> "4";
            case KeyEvent.VK_NUMPAD5 -> "5";
            case KeyEvent.VK_NUMPAD6 -> "6";
            case KeyEvent.VK_NUMPAD7 -> "7";
            case KeyEvent.VK_NUMPAD8 -> "8";
            case KeyEvent.VK_NUMPAD9 -> "9";
            case KeyEvent.VK_PLUS -> "+";
            case KeyEvent.VK_MULTIPLY -> "*";
            default -> "";
        };
    }
}
