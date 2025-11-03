/**
 * Copyright 2025 Langdon Staab
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
package frontEnd;

import utilities.DateUtility;
import utilities.FileUtility;

import java.io.*;
import java.util.Calendar;
import java.util.logging.ConsoleHandler;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Contains static methods for the MainBackendHolder class
 */
final class Util {
    private Util() {
        throw new InstantiationError("This class is uninstantiable.");
    }

    /**
     * Creates a ConsoleHandler to print LOGGER messages to stderr
     *
     * @return The new ConsoleHandler
     */
    static ConsoleHandler getNewHandler() {
        ConsoleHandler newHandler = new ConsoleHandler();
        newHandler.setFormatter(new SimpleFormatter() {
            private static final String FORMAT = "[%s] %s %s: %s\n";

            @Override
            public synchronized String format(LogRecord lr) {
                Calendar cal = DateUtility.calendarFromMillis(lr.getMillis());
                String base = String.format(FORMAT, DateUtility.calendarToTimeString(cal), lr.getLoggerName(), lr.getLevel().getLocalizedName(), lr.getMessage());
                StringBuilder builder = new StringBuilder(base);
                if (lr.getThrown() != null) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    lr.getThrown().printStackTrace(pw);
                    builder.append(sw);
                }
                return builder.toString();
            }
        });
        return newHandler;
    }

    /**
     * Zip a file
     *
     * @param zipFile
     * @param outputFolder
     */
    public static void unZipFile(String zipFile, String outputFolder) throws IOException {
        byte[] buffer = new byte[1024];
        // Create output directory is not exists
        File folder = new File(outputFolder);
        if (!folder.exists()) if (folder.mkdir()) throw new RuntimeException("Could not create directory.");
        // Get the zip file content
        ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));
        // Get the zipped file list entry
        ZipEntry zipEntry = zipInputStream.getNextEntry();
        while (zipEntry != null) {
            String fileName = zipEntry.getName();
            File newFile = new File(FileUtility.joinPath(outputFolder, fileName));
            // create all non exists folders
            // else you will hit FileNotFoundException for compressed folder
            new File(newFile.getParent()).mkdirs();
            FileOutputStream fileOutputStream = new FileOutputStream(newFile);

            int length;
            while ((length = zipInputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, length);
            }
            fileOutputStream.close();
            zipEntry = zipInputStream.getNextEntry();
            zipInputStream.closeEntry();
            zipInputStream.close();
        }
    }

    static void zipDir(File in, String out) {
        if (in == null || out == null) throw new IllegalArgumentException("File(s) may not be null.");
        if (!in.exists()) throw new IllegalArgumentException("Input directory does not not exist.");
        if (!in.isDirectory()) throw new IllegalArgumentException("Not a directory.");
        new FileZipper(in, out);
    }

    private static final class FileZipper {
        private final ZipOutputStream z;

        private FileZipper(File in, String out) {
            try {
                z = new ZipOutputStream(new FileOutputStream(out));
                addFile(in, "");
                z.flush();
                z.finish();
                z.close();
            } catch (FileNotFoundException e) {
                throw new IllegalArgumentException("File not found!\n" + e);
            } catch (IOException e) {
                throw new RuntimeException("I/O Exception!\n" + e);
            } finally {
                FileUtility.deleteFile(in);
            }
        }

        private void addFile(File f, String pathPrefix) throws IOException {
            if (f.isHidden() || !f.exists()) return;
            if (f.isDirectory()) {
                z.putNextEntry(new ZipEntry(pathPrefix + f.getName() + "/"));
                z.closeEntry();
                for (File child : f.listFiles()) {
                    addFile(child, pathPrefix + f.getName() + "/");
                }
                return;
            }
            z.putNextEntry(new ZipEntry(pathPrefix + f.getName()));
            try (BufferedInputStream is = new BufferedInputStream(new FileInputStream(f))) {
                byte[] bytes = new byte[1024];
                int length;
                while ((length = is.read(bytes)) >= 0) {
                    z.write(bytes, 0, length);
                }
            }
            z.closeEntry();
        }
    }
}