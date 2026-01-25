/**
 * Copyright 2025 Langdon Staab and HP Truong
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Langdon Staab
 * @author HP Truong
 */
package utilities;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public final class Desktop {

    private static final Logger LOGGER = Logger.getLogger(Desktop.class.getName());

    private Desktop() {
        throw new InstantiationError("This class is uninstantiable.");
    }

    public static String getClipboard() {
        try {
            return (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
        } catch (HeadlessException | UnsupportedFlavorException | IOException e) {
            LOGGER.log(Level.WARNING, "Unable to retrieve text from clipboard", e);
            return "";
        }
    }

    public static boolean setClipboard(String data) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection selection = new StringSelection(data);
        clipboard.setContents(selection, null);
        return true;
    }

    public static boolean openFile(File file) {
        LOGGER.info("Opening file " + file.getAbsolutePath());
        try {
            java.awt.Desktop.getDesktop().open(file);
            return true;
        } catch (IllegalArgumentException e) {
            LOGGER.warning("File <" + file.getAbsolutePath() + "> does not exist.");
        } catch (Exception ignored) {
        }
        return switch (OSIdentifier.getCurrentOS()) {
            case WINDOWS -> openFileWindows(file);
            case LINUX -> openFileLinux(file);
            case MAC -> openFileOSX(file);
            default -> openWithCommand("xedit", file);
        };
    }

    private static boolean openFileWindows(File file) {
        return openWithCommand("notepad", file);
    }

    private static boolean openFileLinux(File file) {
        if (openWithCommand("xdg-open", file)) {
            return true;
        }
        if (openWithCommand("kde-open", file)) {
            return true;
        }
        if (openWithCommand("gnome-open", file)) {
            return true;
        }
        LOGGER.info("No good text editor was found. Using xedit (sorry)");
        return openWithCommand("xedit", file);
    }

    private static boolean openFileOSX(File file) {
        return openWithCommand("open -e", file);
    }

    private static boolean openWithCommand(String cmd, File file) {
        try {
            Runtime.getRuntime().exec(new String[]{cmd, file.getAbsolutePath()});
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}