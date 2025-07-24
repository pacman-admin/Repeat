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
import core.cli.messages.TaskGroupMessage;
import core.cli.messages.TaskIdentifier;
import core.cli.server.CliRpcCodec;
import core.userDefinedTask.TaskGroup;
import core.userDefinedTask.UserDefinedAction;
import core.webui.webcommon.HttpHandlerWithBackend;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.util.logging.Logger;

public abstract class TaskActionHandler extends HttpHandlerWithBackend {

    private static final Logger LOGGER = Logger.getLogger(TaskActionHandler.class.getName());

    private static final String ACCEPTED_METHOD = "POST";

    @Override
    protected void handleWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws IOException {
        String method = request.getRequestLine().getMethod();
        if (!method.equalsIgnoreCase(ACCEPTED_METHOD)) {
            LOGGER.warning("Ignoring request with unknown method " + method);
            CliRpcCodec.prepareResponse(exchange, 400, "Method must be " + ACCEPTED_METHOD);
            return;
        }

        JsonNode requestData = CliRpcCodec.decodeRequest(getRequestBody(request));
        if (requestData == null) {
            LOGGER.warning("Failed to parse request into JSON!");
            CliRpcCodec.prepareResponse(exchange, 400, "Cannot parse request!");
            return;
        }

        handleTaskActionWithBackend(exchange, requestData);
    }

    protected abstract Void handleTaskActionWithBackend(HttpAsyncExchange exchange, JsonNode request) throws IOException;

    protected UserDefinedAction getTask(TaskGroup group, TaskIdentifier taskIdentifier) {
        UserDefinedAction task = null;
        if (group != null) {
            task = group.getTask(taskIdentifier.getTask().getIndex());
            if (task == null) {
                task = group.getTaskByName(taskIdentifier.getTask().getName());
            }
            return task;
        }

        return backEndHolder.getTaskByName(taskIdentifier.getTask().getName());
    }

    protected TaskGroup getGroup(TaskIdentifier taskIdentifier) {
        return getGroup(taskIdentifier.getGroup());
    }

    protected TaskGroup getGroup(TaskGroupMessage taskGroup) {
        int index = taskGroup.getIndex();
        String name = taskGroup.getName();
        if (index == TaskGroupMessage.UNKNOWN_INDEX && name.isEmpty()) {
            index = 0;
        }

        TaskGroup group = null;
        if (taskGroup != null) {
            group = backEndHolder.getTaskGroup(index);
            if (group == null) {
                group = backEndHolder.getTaskGroupFromName(name);
            }
        }
        return group;
    }
}
