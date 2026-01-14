/*
 * Copyright (c) 2026 Langdon Staab <langdon@langdonstaab.ca>
 *
 * Permission to use, copy, modify, and distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
package core.userDefinedTask;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Clipboard {
    private static final Logger LOGGER = Logger.getLogger(Clipboard.class.getName());

    private Clipboard() {
        //This is a static, uninstantiable class.
    }

    /**
     * Get plain text (if possible) from system clipboard
     *
     * @return the plain text in the clipboard, or empty string if encounter an error
     */
    public static synchronized String get() {
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
    public static synchronized void set(String data) {
        java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection selection = new StringSelection(data);
        clipboard.setContents(selection, null);
    }

    /**
     * Clear the system clipboard
     */
    public static synchronized void clear() {
        set("");
    }
}