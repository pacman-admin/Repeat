package core.userDefinedTask;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Clipboard {
    private Clipboard(){
        //This is a static, uninstantiable class.
    }
    private static final Logger LOGGER = Logger.getLogger(Clipboard.class.getName());
    /**
     * Get plain text (if possible) from system clipboard
     *
     * @return the plain text in the clipboard, or empty string if encounter an error
     */
    public static String get() {
        try {
            return (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
        } catch (HeadlessException | UnsupportedFlavorException | IOException e) {
            LOGGER.log(Level.WARNING, "Unable to retrieve text from clipboard", e);
            return "";
        }
    }
    /**
     * Set a text value into the system clipboard
     *
     * @param data string to copy to the system clipboard
     */
    public static void set(String data) {
        java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection selection = new StringSelection(data);
        clipboard.setContents(selection, null);
    }
}