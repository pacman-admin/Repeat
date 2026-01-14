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
package core.webui.server.handlers;

import core.config.Constants;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class AboutPageHandler extends AbstractUIHttpHandler {

    public AboutPageHandler(ObjectRenderer objectRenderer) {
        super(objectRenderer, AbstractSingleMethodHttpHandler.GET_METHOD);
    }

    @Override
    protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange) throws IOException {
        Map<String, Object> data = new HashMap<>();
        data.put("version", Constants.PROGRAM_VERSION);
        return renderedPage(exchange, "about", data);
    }
}
