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
import core.cli.messages.TaskListMessage;
import core.cli.server.CliRpcCodec;
import core.cli.server.utils.EnumerationUtils;
import core.userDefinedTask.TaskGroup;
import core.userDefinedTask.UserDefinedAction;
import org.apache.http.nio.protocol.HttpAsyncExchange;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class TaskListActionHandler extends TaskActionHandler {

    @Override
    protected Void handleTaskActionWithBackend(HttpAsyncExchange exchange, JsonNode request) throws IOException {
        TaskListMessage message = TaskListMessage.parseJSON(request);
        boolean noGroup = message.getGroup() == null || (message.getGroup().getIndex() == TaskGroupMessage.UNKNOWN_INDEX && message.getGroup().getName().isEmpty());

        if (noGroup) {
            List<String> names = backEndHolder.getTaskGroups().stream().map(TaskGroup::getName).collect(Collectors.toList());
            return CliRpcCodec.prepareResponse(exchange, 200, EnumerationUtils.enumerate(names));
        }
        TaskGroup group = getGroup(message.getGroup());
        if (group == null) {
            return CliRpcCodec.prepareResponse(exchange, 400, "Cannot find group with given arguments.");
        }
        List<String> taskNames = group.getTasks().stream().map(UserDefinedAction::getName).collect(Collectors.toList());
        return CliRpcCodec.prepareResponse(exchange, 200, EnumerationUtils.enumerate(taskNames));
    }
}
