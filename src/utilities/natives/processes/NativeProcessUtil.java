package utilities.natives.processes;

import utilities.OSIdentifier;

/**
 * Provides utility to interact with processes via native APIs.
 */
public final class NativeProcessUtil {

    private NativeProcessUtil() {
        throw new InstantiationError("This class is uninstantiable.");
    }

    public static NativeWindowInfo getActiveWindowInfo() {
        return switch (OSIdentifier.getCurrentOS()) {
            case WINDOWS -> WindowsNativeProcessUtil.getActiveWindowInfo();
            case LINUX -> X11NativeProcessUtil.getActiveWindowInfo();
            case MAC -> OSXNativeProcessUtil.getActiveWindowInfo();
            default -> throw new IllegalStateException("OS is not supported.");
        };
    }

    /**
     * Contains information for native window.
     */
    public static final class NativeWindowInfo {
        private String title;
        // Name of the active process.
        // For OSX this is the active application.
        private String processName;

        static NativeWindowInfo of(String title, String processName) {
            NativeWindowInfo result = new NativeWindowInfo();
            result.title = title;
            result.processName = processName;
            return result;
        }

        public String getTitle() {
            return title;
        }

        public String getProcessName() {
            return processName;
        }
    }
}
