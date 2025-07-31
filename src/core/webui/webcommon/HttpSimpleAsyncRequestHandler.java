/**
 * Copyright 2025 Langdon Staab
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
package core.webui.webcommon;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.BasicAsyncRequestConsumer;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.nio.protocol.HttpAsyncRequestConsumer;
import org.apache.http.nio.protocol.HttpAsyncRequestHandler;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

public abstract class HttpSimpleAsyncRequestHandler implements HttpAsyncRequestHandler<HttpRequest> {

    @Override
    public final void handle(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws HttpException, IOException {
        handleRequest(request, exchange, context);
    }

    public abstract Void handleRequest(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws HttpException, IOException;

    @Override
    public final HttpAsyncRequestConsumer<HttpRequest> processRequest(HttpRequest arg0, HttpContext arg1) {
        // Buffer request content in memory for simplicity.
        return new BasicAsyncRequestConsumer();
    }
}
