package utilities;

import utilities.SubprocessUtility.ExecutionException;

import java.awt.Desktop.Action;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Desktop {

    private static final Logger LOGGER = Logger.getLogger(Desktop.class.getName());

    private Desktop() {
    }

    public static boolean openFile(File file) {
        if (java.awt.Desktop.isDesktopSupported() && java.awt.Desktop.getDesktop().isSupported(Action.OPEN)) {
            try {
                java.awt.Desktop.getDesktop().open(file);
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "IOException when opening file.", e);
                return false;
            }
            return true;
        }

        if (OSIdentifier.IS_WINDOWS) {
            return openFileWindows(file);
        }
        if (OSIdentifier.IS_LINUX) {
            return openFileLinux(file);
        }
        return OSIdentifier.IS_OSX && openFileOSX(file);

    }

    private static boolean openFileWindows(File file) {
        return openWithCommand("explorer", file);
    }

    private static boolean openFileLinux(File file) {
        return openWithCommand("xdg-open", file) || openWithCommand("kde-open", file) || openWithCommand("gnome-open", file);
    }

    private static boolean openFileOSX(File file) {
        return openWithCommand("open", file);
    }

    private static boolean openWithCommand(String cmd, File file) {
        try {
            SubprocessUtility.execute(cmd + " " + file.getAbsolutePath());
        } catch (ExecutionException e) {
            return false;
        }
        return true;
    }
}
