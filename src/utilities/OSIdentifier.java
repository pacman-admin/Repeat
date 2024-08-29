package utilities;

public final class OSIdentifier {

    public static final boolean IS_WINDOWS;
    public static final boolean IS_LINUX;
    public static final boolean IS_OSX;
    private static final String OS_NAME;

    static {
        // See http://lopica.sourceforge.net/os.html
        OS_NAME = System.getProperty("os.name").toLowerCase();
        IS_WINDOWS = OS_NAME.startsWith("win");
        IS_OSX = OS_NAME.startsWith("mac");
        IS_LINUX = OS_NAME.startsWith("linux");
    }

    private OSIdentifier() {
    }
}
