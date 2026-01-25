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

import core.webui.webcommon.HttpHandlerWithBackend;
import core.webui.webcommon.HttpServerUtilities;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;

import java.io.IOException;

public abstract class AbstractSingleMethodHttpHandler extends HttpHandlerWithBackend {

    protected static final String GET_METHOD = "GET";
    protected static final String POST_METHOD = "POST";
    private final String allowedMethod;

    public AbstractSingleMethodHttpHandler(String allowedMethod) {
        this.allowedMethod = allowedMethod;
    }

    @Override
    protected final void handle(HttpRequest request, HttpAsyncExchange exchange) throws IOException {
        if (allowedMethod != null && !request.getRequestLine().getMethod().equalsIgnoreCase(allowedMethod)) {
            HttpServerUtilities.prepareHttpResponse(exchange, 400, "Only " + allowedMethod + " requests are accepted.");
            return;
        }

        handleAllowedRequestWithBackend(request, exchange);
    }

    protected final Void emptySuccessResponse(HttpAsyncExchange exchange){
        return HttpServerUtilities.prepareHttpResponse(exchange, 200, "");
    }

    protected abstract Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange) throws IOException;
}