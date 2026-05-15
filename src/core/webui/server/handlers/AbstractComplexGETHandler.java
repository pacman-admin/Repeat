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
package core.webui.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import core.webui.webcommon.HttpServerUtilities;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public abstract class AbstractComplexGETHandler extends AbstractSimpleHandler {

    protected AbstractComplexGETHandler(String errorMsg) {
        super(AbstractSingleMethodHttpHandler.GET_METHOD, errorMsg);
    }

    protected abstract String handleAsString(Map<String, String> params);

    @Override
    public final String handleAsString(HttpExchange exchange) {
        return handleAsString(HttpServerUtilities.parseGetParameters(exchange.getRequestURI()));
    }
}