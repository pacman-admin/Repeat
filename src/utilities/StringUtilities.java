package utilities;

import org.apache.commons.text.StringEscapeUtils;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Provide static interface to utilities
 *
 * @author HP
 */
public final class StringUtilities {

    /**
     * Private constructor so that no instance is created
     */
    private StringUtilities() {
        throw new IllegalStateException("Cannot create an instance of static class Util");
    }

    /**
     * Returns whether a string is null or empty.
     */
    public static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    /**
     * Join an iterable of string to one string with joiner. If an element
     * of the iterable contains nothing or only space, it will be ignored
     *
     * @param fields iterable of string elements that will be joined.
     * @param joiner delimiter between each element
     * @return One string resulted from the elements joined with joiner
     */
    public static String join(Iterable<String> fields, String joiner) {
        StringBuilder builder = new StringBuilder();
        Iterator<String> iter = fields.iterator();

        while (iter.hasNext()) {
            String next = iter.next();

            boolean valid = !next.replaceAll(" ", "").isEmpty();

            if (valid) {
                builder.append(next);
            }

            if (!iter.hasNext()) {
                break;
            }

            if (valid) {
                builder.append(joiner);
            }
        }
        return builder.toString();
    }

    /**
     * Join an array of string to one string with joiner. If an element
     * of the array contains nothing or only space, it will be ignored
     *
     * @param data   array of string elements that will be joined.
     * @param joiner delimiter between each element
     * @return One string resulted from the elements joined with joiner
     */
    public static String join(String[] data, String joiner) {
        return join(Arrays.asList(data), joiner);
    }

    /**
     * Count occurrence of a character in a String
     *
     * @param input a String
     * @param c     character to count
     * @return number of occurrence of the character in the string. Return 0 if input is null.
     */
    public static int countOccurrence(String input, char c) {
        if (input == null) {
            return 0;
        }

        int count = 0;
        for (int i = 0; i < input.length(); i++) {
            count = (input.charAt(i) == c) ? (count + 1) : count;
        }

        return count;
    }

    /**
     * Calculate the Levenshtein distance between two strings using dynamic programming.
     * This implementation is based on the pseudocode presented on Wikipedia.
     *
     * @param l first string
     * @param r second string
     * @return the Levenshtein distance between two strings
     */
    public static int levenshteinDistance(String l, String r) {
        if (l == null && r == null) {
            return 0;
        }
        if (l == null) {
            return r.length();
        }
        if (r == null) {
            return l.length();
        }

        if (l.isEmpty() && r.isEmpty()) {
            return 0;
        }
        if (l.isEmpty()) {
            return r.length();
        }
        if (r.isEmpty()) {
            return l.length();
        }

        int m = l.length();
        int n = r.length();

        int[][] d = new int[m + 1][n + 1];
        for (int i = 0; i < m; i++) {
            d[i][0] = i;
        }

        for (int j = 0; j < n; j++) {
            d[0][j] = j;
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                char cl = l.charAt(i - 1);
                char cr = r.charAt(j - 1);

                int cost = cl == cr ? 0 : 1;

                d[i][j] = Math.min(Math.min(d[i - 1][j] + 1, d[i][j - 1] + 1), d[i - 1][j - 1] + cost);

                // Uncomment this for Optimal Alignment String Distance calculation
                // Transposition
//				if (i > 1 && j > 1 && cl == r.charAt(j-2) && l.charAt(i-2) == cr) {
//					d[i][j] = Math.min(d[i][j], d[i-2][j-2]);
//				}
            }
        }

        return d[m][n];
    }

    /**
     * Simple conversion from snake case to camel case.
     * This does not take into account special cases like
     * "a__3".
     */
    public static String toCamelCase(String snakeCase) {
        StringBuilder sb = new StringBuilder(snakeCase);
        for (int i = 0; i < sb.length(); i++) {
            if (sb.charAt(i) == '_') {
                sb.deleteCharAt(i);
                sb.replace(i, i + 1, String.valueOf(Character.toUpperCase(sb.charAt(i))));
            }
        }

        return sb.toString();
    }

    /**
     * Capitalize the first letter of every word in a string.
     */
    public static String title(String s) {
        if (s == null) {
            return null;
        }
        if (s.isEmpty()) {
            return s;
        }
        StringBuilder sb = new StringBuilder();
        Arrays.stream(s.split("\\s+")).forEach(part -> sb.append(Character.toTitleCase(part.charAt(0))).append(part.substring(1)).append(" "));
        return sb.toString().trim();
    }

    /**
     * Returns an HTML-escaped version of the string.
     */
    public static String escapeHtml(String s) {
        return StringEscapeUtils.escapeHtml4(s);
    }
}