package utilities;

public class OSIdentifier {

	private static final String OS_NAME;
	public static final boolean IS_WINDOWS;
    private static final boolean IS_UNIX;
    public static final boolean IS_LINUX;
    public static final boolean IS_OSX;
    private static final boolean IS_UNKNOWN;

	static {
		// See http://lopica.sourceforge.net/os.html
		OS_NAME = System.getProperty("os.name").toLowerCase();
		IS_WINDOWS = OS_NAME.startsWith("win");
		IS_UNIX = !IS_WINDOWS;
		IS_OSX = OS_NAME.startsWith("mac");
		IS_LINUX = OS_NAME.startsWith("linux");
		IS_UNKNOWN = !IS_WINDOWS && !IS_LINUX && !IS_OSX;
	}

	private OSIdentifier() {}
}
