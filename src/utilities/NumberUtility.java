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
    public static boolean isPositiveInteger(String input) {
        return isInteger(input) && Long.parseLong(input) > 0;
    }

    public static boolean isNonNegativeInteger(String input) {
        return isInteger(input) && !input.startsWith("-");
    }

    private static boolean isInteger(String input) {
        if(StringUtilities.isNullOrEmpty(input))
            return false;
        input = input.replaceAll(",", "");
        if (input.startsWith("-")) {
            input = input.substring(1);
        }
        for (char c : input.toCharArray()){
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