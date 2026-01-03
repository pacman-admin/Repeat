package core.userDefinedTask;

/**
 * Use core.userDefinedTask.Clipboard instead
 */
@Deprecated
public class Tools {
    private Tools() {
        //This class is uninstantiable
    }

    /**
     * Get plain text (if possible) from system clipboard
     *
     * @return the plain text in the clipboard, or empty string if encounter an error
     */
    @Deprecated
    public static String getClipboard() {
        return Clipboard.get();
    }

    /**
     * Set a text value into the system clipboard
     *
     * @param data string to copy to the system clipboard
     */
    @Deprecated
    public static void setClipboard(String data) {
        Clipboard.set(data);
    }
}