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
package core.cli.server.handlers;

import argo.jdom.JsonNode;
import core.cli.messages.TaskExecuteMessage;
import core.cli.server.CliRpcCodec;
import core.userDefinedTask.TaskGroup;
import core.userDefinedTask.UserDefinedAction;
import org.apache.http.nio.protocol.HttpAsyncExchange;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaskExecuteActionHandler extends TaskActionHandler {

    private static final Logger LOGGER = Logger.getLogger(TaskExecuteActionHandler.class.getName());

    @Override
    protected Void handleTaskActionWithBackend(HttpAsyncExchange exchange, JsonNode request) throws IOException {
        TaskExecuteMessage message = TaskExecuteMessage.parseJSON(request);
        if (message.getTaskIdentifier() == null) {
            return CliRpcCodec.prepareResponse(exchange, 400, "Missing task identifier.");
        }

        TaskGroup group = getGroup(message.getTaskIdentifier());
        UserDefinedAction task = getTask(group, message.getTaskIdentifier());
        if (task == null) {
            return CliRpcCodec.prepareResponse(exchange, 400, "Unable to find task.");
        }

        try {
            LOGGER.info("Executing action " + task.getName());
            task.trackedAction(backEndHolder.getCoreProvider().get());
        } catch (InterruptedException e) {
            LOGGER.log(Level.WARNING, "Task interrupted.", e);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Exception when executing task.", e);
            return CliRpcCodec.prepareResponse(exchange, 500, "Exception when executing task: " + e.getMessage());
        }
        return CliRpcCodec.prepareResponse(exchange, 200, "Successfully executed task.");
    }
}
