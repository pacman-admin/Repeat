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
import core.webui.server.handlers.AbstractPOSTHandler;
import core.webui.webcommon.HttpServerUtilities;
import frontEnd.Backend;
import org.apache.http.HttpRequest;
import utilities.NumberUtility;

public class RunTaskHandler extends AbstractPOSTHandler {

    public RunTaskHandler() {
        super("Could not run task.");
    }

    @Override
    protected String handle(HttpRequest request) {
        JsonNode requestMessage = HttpServerUtilities.parsePostParameters(request);
        if (requestMessage == null) {
            throw new IllegalArgumentException("Unable to parse JSON from request parameter.");
        }

        RunTaskRequest requestData = RunTaskRequest.of();
        if (!requestData.parse(requestMessage)) {
            throw new IllegalArgumentException("Unable to parse POST request parameters.");
        }
        String id = requestData.getId();
        RunActionConfig runConfig = Backend.getRunActionConfig();
        ActionExecutionRequest executionRequest = ActionExecutionRequest.of(runConfig.getRepeatCount(), runConfig.getDelayMsBetweenRepeats());

        if (requestData.getRunConfig() != null) { // Custom run config is provided.
            String repeatCountString = requestData.getRunConfig().getRepeatCount();
            if (!NumberUtility.isPositiveInteger(repeatCountString)) {
                throw new IllegalArgumentException("Repeat count must be a positive integer.");
            }
            int repeatCount = Integer.parseInt(repeatCountString);

            String delayMsString = requestData.getRunConfig().getDelayMsBetweenRepeat();
            if (!NumberUtility.isNonNegativeInteger(delayMsString)) {
                throw new IllegalArgumentException("Delay in milliseconds must be a non-negative integer.");
            }
            long delayMs = Long.parseLong(delayMsString);
            executionRequest = ActionExecutionRequest.of(repeatCount, delayMs);
        }

        UserDefinedAction action = Backend.getTask(id);
        if (action == null) {
            throw new NullPointerException("No such task with ID " + id + ".");
        }
        Backend.actionExecutor.startExecutingAction(executionRequest, action);
        return id;
    }
}