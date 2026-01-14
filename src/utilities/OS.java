/*
 * Copyright (c) 2025 Langdon Staab <langdon@langdonstaab.ca>
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

public enum OS {
    WINDOWS(true), LINUX(false), MAC(true), OTHER(false);
    public final boolean isClipboardSupported;

    OS(boolean canUseClipboard) {
        isClipboardSupported = canUseClipboard;
    }

    static OS getCurrentOS() {
        String OSName = System.getProperty("os.name").toLowerCase().trim();
        //System.out.println("Your OS is: " + OSName);
        if (OSName.startsWith("mac")) {
            return OS.MAC;
        }
        if (OSName.startsWith("linux")) {
            return OS.LINUX;
        }
        if (OSName.startsWith("windows")) {
            return OS.WINDOWS;
        }
        return OS.OTHER;
    }
}