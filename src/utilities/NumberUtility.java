/**
 * Copyright 2025 Langdon Staab
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Langdon Staab
 * @author HP Truong
 */
package utilities;

public final class NumberUtility {
    /*public static boolean equalDouble(double a, double b) {
        return Math.abs(a - b) < 0.00001d;
    }

    public static boolean equalFloat(float a, float b) {
        return Math.abs(a - b) < 0.00001f;
    }

    public static int getDigit(int number, int digit) {//0 means lsb
        String num = number + "";
        int lsb = num.length() - 1;
        lsb -= digit;
        if (lsb >= 0) {
            return num.charAt(lsb) - '0';
        } else {
            return -1;
        }
    }

    public static boolean isInteger(double input) {
        return (input == Math.floor(input)) && !Double.isInfinite(input);
    }*/
    public static boolean isPositiveInteger(String input) {
        return isInteger(input) && Long.parseLong(input) > 0;
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

    /*
    public static boolean inRange(int a, int lower, int upper) {
        return (a >= lower) && (a <= upper);
    }

    public static boolean inRange(double a, double lower, double upper) {
        return (a >= lower) && (a <= upper);
    }*/
    public static float fromIEEE754Binary(String binary) {
        int integer = (int) Long.parseLong(binary, 2);
        return Float.intBitsToFloat(integer);
    }
}