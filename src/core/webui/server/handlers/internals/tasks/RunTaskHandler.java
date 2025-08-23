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
package core.webui.server.handlers.internals.tasks;

import argo.jdom.JsonNode;
import core.userDefinedTask.UserDefinedAction;
import core.userDefinedTask.internals.ActionExecutionRequest;
import core.userDefinedTask.internals.RunActionConfig;
import core.webui.server.handlers.AbstractSingleMethodHttpHandler;
import core.webui.webcommon.HttpServerUtilities;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;
import utilities.NumberUtility;

import java.io.IOException;

public class RunTaskHandler extends AbstractSingleMethodHttpHandler {

    public RunTaskHandler() {
        super(AbstractSingleMethodHttpHandler.POST_METHOD);
    }

    @Override
    protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws IOException {
        JsonNode requestMessage = HttpServerUtilities.parsePostParameters(request);
        if (requestMessage == null) {
            return HttpServerUtilities.prepareTextResponse(exchange, 400, "Unable to parse JSON from request parameter.");
        }

        RunTaskRequest requestData = RunTaskRequest.of();
        if (!requestData.parse(requestMessage)) {
            return HttpServerUtilities.prepareTextResponse(exchange, 400, "Unable to parse POST request parameters.");
        }
        String id = requestData.getId();
        RunActionConfig runConfig = backEndHolder.getRunActionConfig();
        ActionExecutionRequest executionRequest = ActionExecutionRequest.of(runConfig.getRepeatCount(), runConfig.getDelayMsBetweenRepeats());

        if (requestData.getRunConfig() != null) { // Custom run config is provided.
            String repeatCountString = requestData.getRunConfig().getRepeatCount();
            if (!NumberUtility.isPositiveInteger(repeatCountString)) {
                return HttpServerUtilities.prepareTextResponse(exchange, 400, "Repeat count must be a positive integer.");
            }
            int repeatCount = Integer.parseInt(repeatCountString);

            String delayMsString = requestData.getRunConfig().getDelayMsBetweenRepeat();
            if (!NumberUtility.isNonNegativeInteger(delayMsString)) {
                return HttpServerUtilities.prepareTextResponse(exchange, 400, "Delay in milliseconds must be a non-negative integer.");
            }
            long delayMs = Long.parseLong(delayMsString);
            executionRequest = ActionExecutionRequest.of(repeatCount, delayMs);
        }

        UserDefinedAction action = backEndHolder.getTask(id);
        if (action == null) {
            return HttpServerUtilities.prepareTextResponse(exchange, 404, "No such task with ID " + id + ".");
        }
        backEndHolder.getActionExecutor().startExecutingAction(executionRequest, action);

        return HttpServerUtilities.prepareTextResponse(exchange, 200, "");
    }
}