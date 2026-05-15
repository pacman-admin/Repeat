/**
 * Copyright 2025 Langdon Staab and HP Truong
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
import core.webui.webcommon.HttpServerUtilities;



import java.io.IOException;
import java.util.Map;

public abstract class AbstractBooleanConfigHttpHandler extends AbstractSingleMethodHttpHandler {

    protected AbstractBooleanConfigHttpHandler() {
        super(AbstractSingleMethodHttpHandler.POST_METHOD);
    }

    @Override
    public final void handleAllowedRequestWithBackend(HttpExchange exchange) {
        Map<String, String> params = HttpServerUtilities.parseSimplePostParameters(exchange);
        if (params == null) {
            HttpServerUtilities.prepareHttpResponse(exchange, 400, "Failed to parse POST parameters."); 
return;
        }
        String value = params.get("value");
        if (value == null) {
            HttpServerUtilities.prepareHttpResponse(exchange, 400, "Missing value."); 
return;
        }
        boolean enabled = value.equalsIgnoreCase("true");
        handleAllowedRequestWithBackendAndValue(exchange, enabled);
    }

    protected abstract void handleAllowedRequestWithBackendAndValue(HttpExchange exchange, boolean value);
}
