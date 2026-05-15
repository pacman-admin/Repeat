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
package core.webui.server.handlers.internals.tasks;

import argo.jdom.JsonNode;
import com.sun.net.httpserver.HttpExchange;
import core.userDefinedTask.internals.RunActionConfig;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.server.handlers.internals.tasks.RunTaskRequest.RunConfig;
import core.webui.webcommon.HttpServerUtilities;
import frontEnd.Backend;
import utilities.NumberUtility;

public final class SaveRunTaskConfigHandler extends AbstractSingleMethodHttpHandler {

    public SaveRunTaskConfigHandler() {
        super(AbstractSingleMethodHttpHandler.POST_METHOD);
    }

    @Override
    public void handleAllowedRequestWithBackend(HttpExchange exchange) {
        JsonNode requestMessage = HttpServerUtilities.parsePostParameters(exchange);
        if (requestMessage == null) {
            HttpServerUtilities.prepareTextResponse(exchange, 400, "Unable to parse JSON from request parameter.");
            return;
        }

        RunConfig config = RunConfig.of();
        config.parse(requestMessage);
        String repeatCountString = config.getRepeatCount();
        if (!NumberUtility.isPositiveInteger(repeatCountString)) {
            HttpServerUtilities.prepareTextResponse(exchange, 400, "Repeat count must be a positive integer.");
            return;
        }
        int repeatCount = Integer.parseInt(repeatCountString);

        String delayMsString = config.getDelayMsBetweenRepeat();
        if (!NumberUtility.isNonNegativeInteger(delayMsString)) {
            HttpServerUtilities.prepareTextResponse(exchange, 400, "Delay in milliseconds must be a non-negative integer.");
            return;
        }
        long delayMs = Long.parseLong(delayMsString);

        Backend.setRunActionConfig(RunActionConfig.of(repeatCount, delayMs));
        HttpServerUtilities.prepareTextResponse(exchange, 200, "");
        return;
    }
}
