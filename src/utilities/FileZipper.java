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
package utilities;

import java.io.*;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class FileZipper {
    private final ZipOutputStream z;

    public FileZipper(File in, String out) {
        try {
            z = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(out)));
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
        //System.out.println("Adding '" + f.getName() + "'...");
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