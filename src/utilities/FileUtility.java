package utilities;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Provide file reading and writing utilities
 * This is a static class. No instance should be created
 *
 * @author HP Truong
 */
public final class FileUtility {

    private static final Logger LOGGER = Logger.getLogger(FileUtility.class.getName());

    /**
     * Private constructor so that no instance is created
     */
    private FileUtility() {
        throw new InstantiationError("This class is uninstantiable.");
    }

    /**
     * Check if file exists and is not a directory
     *
     * @param file file to check
     * @return if file exists
     */
    public static boolean fileExists(File file) {
        return file.exists() && !file.isDirectory();
    }


    public static String getPath(String path) {
        return new File(path).getAbsolutePath();

    }

    /**
     * Get relative path of a file with respect to a working directory
     *
     * @param workDirectory the directory that will be reference
     * @param target        file whose path will be compared to that of the directory
     * @return relative path to the file from the directory. If no relative path exists, provide absolute path to file
     */
    private static String getRelativePath(File workDirectory, File target) {
        if (target.getAbsolutePath().startsWith(workDirectory.getAbsolutePath())) {
            String relativePath = target.getAbsolutePath().substring(workDirectory.getAbsolutePath().length() + 1);
            relativePath = relativePath.replaceAll(Pattern.quote(File.separator), "/");
            return relativePath;
        } else {
            return target.getAbsolutePath();
        }
    }

    /**
     * Get relative path of a file with respect to current working directory
     *
     * @param target file whose path will be compared to that of current working directory
     * @return relative path to the file from current working directory. If no relative path exists, provide absolute path to file
     */
    public static String getRelativePwdPath(File target) {
        return getRelativePath(new File(""), target);
    }

    /**
     * Remove the last extension of file. If no extension found then return the input file
     * E.g. a.out.log --> a.out
     * a.diff --> a
     *
     * @param file file to remove extension
     * @return file with last extension removed
     */
    public static File removeExtension(File file) {
        String absolutePath = file.getAbsolutePath();
        if (absolutePath.contains(".")) {
            return new File(absolutePath.substring(0, absolutePath.lastIndexOf('.')));
        } else {
            return file;
        }
    }

    /**
     * Append an extension to a file. If extension does not contain a dot, it will be automatically added
     *
     * @param file      file to add extension
     * @param extension extension to be add
     * @return file with extension: fileName.extension
     */
    public static File addExtension(File file, String extension) {
        if (extension.startsWith(".")) {
            return new File(file.getAbsolutePath() + extension);
        } else {
            return new File(file.getAbsolutePath() + "." + extension);
        }
    }

    /**
     * Recursively walk through the path and return all found files (not directory)
     *
     * @param path path to be walked
     * @return list of files found. This does not include the directory names
     */
    public static List<File> walk(String path) {
        LinkedList<File> output = new LinkedList<>();

        File root = new File(path);
        File[] list = root.listFiles();

        if (list == null) {
            return output;
        }

        for (File f : list) {
            if (f.isDirectory()) {
                output.addAll(walk(f.getAbsolutePath()));
            } else {
                output.addLast(f);
            }
        }

        return output;
    }

    /**
     * Copy source file to destination file. Create directory if destination directory does not exist
     *
     * @param source source file
     * @param dest   destination file
     */
    public static void copyFile(File source, File dest) {
        File parentDest = dest.getParentFile();

        if (!parentDest.exists()) {
            if (!createDirectory(parentDest.getAbsolutePath())) {
                return;
            }
        }

        try {
            Files.copy(source.toPath(), dest.toPath());
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Encountered an IOException while copying" + e, e);
        }
    }

    /**
     * Delete a file or directory. FOLLOW symlink
     *
     * @param toDelete file to delete
     * @return if delete successful
     */
    public static boolean deleteFile(File toDelete) {
        boolean result = true;
        if (toDelete.isDirectory()) {
            for (File c : Objects.requireNonNull(toDelete.listFiles())) {
                result &= deleteFile(c);
            }
        }

        if (!toDelete.delete()) {
            LOGGER.log(Level.SEVERE, "Cannot delete file " + toDelete.getAbsolutePath());
            return false;
        }

        return result;
    }

    /**
     * Create directory if not exists
     *
     * @param directory full path of the directory
     * @return if creation succeed
     */
    public static boolean createDirectory(String directory) {
        File theDir = new File(directory);
        // if the directory does not exist, create it
        if (!theDir.exists()) {
            boolean result;
            try {
                result = theDir.mkdirs();
            } catch (SecurityException e) {
                LOGGER.log(Level.WARNING, "Could not create director(y/ies).\nPlease make sure you have permission to create directories\n" + e, e);
                // handle it
                return false;
            }
            return result;
        }
        return true;
    }

    /**
     * Read a plain text file.
     *
     * @param file file that will be read
     * @return StringBuffer the read result.
     */
    public static StringBuffer readFromFile(File file) {
        StringBuffer output = new StringBuffer();

        try (FileInputStream fr = new FileInputStream(file)) {

            InputStreamReader char_input = new InputStreamReader(fr, StandardCharsets.UTF_8.newDecoder());

            BufferedReader br = new BufferedReader(char_input);

            while (true) {
                String in = br.readLine();
                if (in == null) {
                    break;
                }
                output.append(in).append("\n");
            }

            br.close();

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, null, e);
            return null;
        }
        return output;
    }

    /**
     * Read a plain text file.
     *
     * @param filePath path to the file
     * @return text content of the file
     */
    public static StringBuffer readFromFile(String filePath) {
        return readFromFile(new File(filePath));
    }

    /**
     * Read plain text data from an InputStream
     *
     * @param inputStream input stream to be read from
     * @return text data retrieved from stream
     */
    public static StringBuffer readFromStream(InputStream inputStream) {
        StringBuffer output = new StringBuffer();
        if (inputStream == null) {
            return output;
        }

        InputStreamReader char_input = new InputStreamReader(inputStream, StandardCharsets.UTF_8.newDecoder());
        BufferedReader br = new BufferedReader(char_input);
        try {
            while (true) {
                String in = br.readLine();

                if (in == null) {
                    break;
                }
                output.append(in).append("\n");
            }
            return output;
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, null, e);
        } finally {
            try {
                char_input.close();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, null, e);
            }

            try {
                br.close();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, null, e);
            }
        }

        return output;
    }

    /**
     * Write a content to a file. The file will be created if it does not exist
     *
     * @param content content that will be written to file using UTF-8 format
     * @param file    target file
     * @param append  will the content be appended to the file or overwritten old data in file (if exists)
     * @return return if write successfully
     */
    public static boolean writeToFile(String content, File file, boolean append) {
        if (!fileExists(file)) {
            if (file.getParentFile() != null) {
                if (!createDirectory(file.getParentFile().getAbsolutePath())) {
                    return false;
                }
            }

            try {
                file.createNewFile();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, null, e);
                return false;
            }
        }

        try (OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(file, append), StandardCharsets.UTF_8)) {
            try {
                Writer bw = new BufferedWriter(fw);


                bw.write(content);
                bw.flush();
                bw.close();
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
                return false;
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    /**
     * Write a content to a file
     *
     * @param content content that will be written to file using UTF-8 format
     * @param file    target file
     * @param append  will the content be appended to the file or overwritten old data in file (if exists)
     * @return return if write successfully
     */
    public static boolean writeToFile(StringBuffer content, File file, boolean append) {
        try (OutputStreamWriter fw = new OutputStreamWriter(new FileOutputStream(file, append), StandardCharsets.UTF_8)) {
            try {
                Writer bw = new BufferedWriter(fw);

                bw.write(content.toString());
                bw.flush();
                bw.close();
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
                return false;
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    /**
     * Remove a file
     *
     * @param file file to be removed
     * @return if removal is successful. Throw IOException if encounters error
     */
    public static boolean removeFile(File file) {
        return !fileExists(file) || file.delete();
    }

    /**
     * IO combining two paths
     *
     * @param path1 first path
     * @param path2 second path
     * @return a path created by joining first path and second path
     */
    private static String joinPath(String path1, String path2) {
        File file1 = new File(path1);
        File file2 = new File(file1, path2);
        return file2.getPath();
    }

    /**
     * IO combining paths
     *
     * @param paths array of paths
     * @return a path created by joining all the paths
     */
    public static String joinPath(String... paths) {
        if (paths.length == 0) {
            return "";
        } else {
            String output = paths[0];
            for (int i = 1; i < paths.length; i++) {
                output = joinPath(output, paths[i]);
            }
            return output;
        }
    }
}