package utilities.natives.processes;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Provides interaction with OSX processes via AppleScript.
 */
final class OSXNativeProcessUtil {

    private static final Logger LOGGER = Logger.getLogger(OSXNativeProcessUtil.class.getName());

    private static final String[] ACTIVE_WINDOW = new String[]{
            "osascript",
            "-e", "global frontApp, frontAppName, windowTitle",
            "-e", "set windowTitle to \"\"",
            "-e", "tell application \"System Events\"",
            "-e", "set frontApp to first application process whose frontmost is true",
            "-e", "set frontAppName to name of frontApp",
            "-e", "tell process frontAppName",
            "-e", "tell (1st window whose value of attribute \"AXMain\" is true)",
            "-e", "set windowTitle to value of attribute \"AXTitle\"",
            "-e", "end tell",
            "-e", "end tell",
            "-e", "log windowTitle",
            "-e", "log frontAppName",
            "-e", "end",
    };

    private OSXNativeProcessUtil() {
        throw new InstantiationError("This class is uninstantiable.");
    }

    public static NativeProcessUtil.NativeWindowInfo getActiveWindowInfo() {
        String execResult = executeActiveWindowTitleCmd();
        if (execResult.isBlank()) {
            return NativeProcessUtil.NativeWindowInfo.of("", "");
        }

        execResult = execResult.trim();
        String[] parts = execResult.split("\n");
        if (parts.length != 2) {
            LOGGER.warning("Error parsing exec result to get active window info: " + execResult);
            return NativeProcessUtil.NativeWindowInfo.of(execResult, "");
        }

        return NativeProcessUtil.NativeWindowInfo.of(parts[0], parts[1]);
    }

    private static String executeActiveWindowTitleCmd() {
        try {
            Process p = Runtime.getRuntime().exec(ACTIVE_WINDOW);
            p.waitFor();
            BufferedReader stdErr = p.errorReader();
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = stdErr.readLine()) != null) {
                sb.append(line).append(System.lineSeparator());
            }
            stdErr.close();
            return sb.toString();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
//        return "";
    }
}
