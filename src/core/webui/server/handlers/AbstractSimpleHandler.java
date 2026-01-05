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

import core.webui.webcommon.HttpServerUtilities;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

abstract class AbstractSimpleHandler extends AbstractSingleMethodHttpHandler {
    private final String errorMessage;

    AbstractSimpleHandler(String type, String errorMsg) {
        super(type);
        if (errorMsg.isBlank()) throw new IllegalArgumentException("Error message must be a String.");
        errorMessage = errorMsg;
    }

    abstract String handle(HttpRequest r);

    private String getErrorMsg(Exception e) {
        return errorMessage + "\n" + e.getMessage();
    }

    protected final Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) {
        try {
            String data = handle(request);
            return HttpServerUtilities.prepareTextResponse(exchange, 200, data);
        } catch (NullPointerException e) {
            return HttpServerUtilities.prepareTextResponse(exchange, 404, getErrorMsg(e));
        } catch (IllegalArgumentException e) {
            return HttpServerUtilities.prepareTextResponse(exchange, 400, getErrorMsg(e));
        } catch (Exception e) {
            return HttpServerUtilities.prepareTextResponse(exchange, 500, getErrorMsg(e));
        }
    }
}