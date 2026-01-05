package core.config;

import java.awt.event.KeyEvent;
import java.util.logging.Level;

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

/**
 * Holds all Configuration-related constants
 * @author Langdon Staab
 * @author HP Truong
 */
public final class Constants {
    public static final int DEFAULT_SERVER_PORT = 8080;
    public static final String PROGRAM_VERSION = "6.0.0";
    public static final int HALT_TASK = KeyEvent.VK_ESCAPE; // This should be hardcoded, and must not be changed
    public static final String CURRENT_CONFIG_VERSION = "3.0";
    public static final String CONFIG_FILE_NAME = "config.json";
    public static final String EXPORTED_CONFIG_FILE_NAME = "exported_" + CONFIG_FILE_NAME;

    private Constants() {
        //This class is uninstantiable
    }
}