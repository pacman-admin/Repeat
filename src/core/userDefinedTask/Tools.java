package core.userDefinedTask;

import core.userDefinedTask.internals.DefaultTools;
import core.userDefinedTask.internals.ITools;
import core.userDefinedTask.internals.LocalTools;

public class Tools {

    private Tools() {
        throw new InstantiationError("This class is uninstantiable.");
    }

    public static ITools local() {
        return LocalTools.of();
    }

    /**
     * Get plain text (if possible) from system clipboard
     *
     * @return the plain text in the clipboard, or empty string if encounter an error
     */
    public static String getClipboard() {
        return DefaultTools.get().getClipboard();
    }

    /**
     * Set a text value into the system clipboard
     *
     * @param data string to copy to the system clipboard
     */
    public static void setClipboard(String data) {
        DefaultTools.get().setClipboard(data);
    }
}