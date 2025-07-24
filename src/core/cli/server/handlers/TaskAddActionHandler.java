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
import core.cli.messages.TaskAddMessage;
import core.cli.server.CliRpcCodec;
import core.userDefinedTask.TaskGroup;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import utilities.FileUtility;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class TaskAddActionHandler extends TaskActionHandler {

    private static final Logger LOGGER = Logger.getLogger(TaskAddActionHandler.class.getName());

    @Override
    protected Void handleTaskActionWithBackend(HttpAsyncExchange exchange, JsonNode request) throws IOException {
        TaskAddMessage message = TaskAddMessage.parseJSON(request);
        if (message.getTaskIdentifier() == null) {
            return CliRpcCodec.prepareResponse(exchange, 400, "Missing task identifier.");
        }

        if (message.getTaskIdentifier().getTask().getName().isEmpty()) {
            return CliRpcCodec.prepareResponse(exchange, 400, "Empty task name.");
        }

        if (message.getFilePath().isEmpty()) {
            return CliRpcCodec.prepareResponse(exchange, 400, "No source file.");
        }

        TaskGroup group = getGroup(message.getTaskIdentifier());
        if (group == null) {
            return CliRpcCodec.prepareResponse(exchange, 400, "Unable to identify task group.");
        }

        Path path = Paths.get(message.getFilePath());
        if (!Files.isRegularFile(path)) {
            return CliRpcCodec.prepareResponse(exchange, 400, "Path " + path + " is not a file.");
        }

        String source = FileUtility.readFromFile(path.toFile()).toString();
        if (source == null) {
            return CliRpcCodec.prepareResponse(exchange, 500, "Unable to read source file.");
        }

        if (!backEndHolder.compileSourceAndSetCurrent(source, message.getTaskIdentifier().getTask().getName())) {
            return CliRpcCodec.prepareResponse(exchange, 500, "Unable to compile source file.");
        }

        backEndHolder.addCurrentTask(group);
        LOGGER.info("Added new task from file " + path.toAbsolutePath());
        return CliRpcCodec.prepareResponse(exchange, 200, "Successfully added new task.");
    }
}
