package utilities;


public final class NumberUtility {

    private NumberUtility() {
    }

    public static boolean isNegativeInteger(String input) {
        return !isInteger(input) || Long.parseLong(input) <= 0;
    }

    public static boolean isNonNegativeInteger(String input) {
        return isInteger(input) && !input.startsWith("-");
    }

    private static boolean isInteger(String input) {
        if (input == null) {
            return false;
        }

        if (input.isEmpty()) {
            return false;
        }

        input = input.replaceAll(",", "");

        if (input.startsWith("-")) {
            input = input.substring(1);
        }

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c > '9' || c < '0') {
                return false;
            }
        }

        return true;
    }

    public static boolean isDouble(String input) {
        if (StringUtilities.countOccurrence(input, '.') > 1) {
            return false;
        }
        input = input.replaceAll("\\.", "");

        return isInteger(input);
    }

    public static float fromIEEE754Binary(String binary) {
        int integer = (int) Long.parseLong(binary, 2);
        return Float.intBitsToFloat(integer);
    }
}
