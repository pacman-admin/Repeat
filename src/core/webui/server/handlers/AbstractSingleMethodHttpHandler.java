/**
 * Copyright 2026 Langdon Staab and HP Truong
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
package core.webui.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import core.webui.webcommon.HTTPLogger;
import com.sun.net.httpserver.HttpHandler;
import core.webui.webcommon.HttpServerUtilities;


import java.io.IOException;

public abstract class AbstractSingleMethodHttpHandler implements HttpHandler {

    protected static final String GET_METHOD = "GET";
    protected static final String POST_METHOD = "POST";
    private final static HTTPLogger LOGGER = new HTTPLogger("Could not add action step");
    private final String allowedMethod;

    public AbstractSingleMethodHttpHandler(String allowedMethod) {
        this.allowedMethod = allowedMethod;
    }

    @Override
    public void handle(HttpExchange exchange) {
        if (allowedMethod != null && !exchange.getRequestMethod().equalsIgnoreCase(allowedMethod)) {
            HttpServerUtilities.prepareHttpResponse(exchange, 400, "Only " + allowedMethod + " requests are accepted.");
            return;
        }
        LOGGER.exec(() -> {
            try {
                handleAllowedRequestWithBackend(exchange);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, exchange);
    }

    protected final void emptySuccessResponse(HttpExchange exchange) {
        HttpServerUtilities.prepareHttpResponse(exchange, 200, "");
    }

    protected abstract void handleAllowedRequestWithBackend(HttpExchange exchange) throws IOException;
}