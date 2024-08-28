package utilities;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Provide static interface to exception utilities.
 *
 * @author HP
 */
public final class ExceptionsUtility {

    private ExceptionsUtility() {
    }

    public static String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
