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
package core.webui.server.handlers.internals.taskcreation;

import core.webui.server.handlers.AbstractPOSTHandler;
import core.webui.webcommon.HttpServerUtilities;
import frontEnd.Backend;
import org.apache.http.HttpRequest;

import java.nio.charset.StandardCharsets;

public final class ActionEditSourceHandler extends AbstractPOSTHandler {
    public ActionEditSourceHandler() {
        super("Could not open source code in editor");
    }

    protected String handle(HttpRequest request) {
        byte[] data = HttpServerUtilities.getPostContent(request);
        if (data == null) throw new IllegalArgumentException("Source code may not be null.");
        Backend.editSource(new String(data, StandardCharsets.UTF_8));
        return "Opened source code in default editor.";
    }
}