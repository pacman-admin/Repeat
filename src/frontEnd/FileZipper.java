package frontEnd;

import utilities.FileUtility;

import java.io.*;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

final class FileZipper {
    private final ZipOutputStream z;

    FileZipper(File in, String out) {
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
            for (File child : Objects.requireNonNull(f.listFiles())) {
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