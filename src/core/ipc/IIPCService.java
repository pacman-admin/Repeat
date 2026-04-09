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
package core.ipc;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import utilities.ILoggable;

import java.io.IOException;
import java.net.ServerSocket;

import static core.config.Constants.DEFAULT_SERVER_PORT;

public abstract class IIPCService implements ILoggable {
    protected int port = DEFAULT_SERVER_PORT;

    public static boolean portAvailable(int port) {
        try {
            ServerSocket socket = new ServerSocket(port);
            socket.close();
            return true;
        } catch (IOException ignored) {
        }
        return false;
    }

    /**
     * Specific configuration parameters for this ipc service.
     *
     * @return the json node containing configuration parameters for this ipc service.
     */
    public JsonNode getSpecificConfig() {
        return JsonNodeFactories.object(JsonNodeFactories.field("port", JsonNodeFactories.number(port)));
    }

    /**
     * Extract internal configuration parameters for this ipc service.
     *
     * @param node the json node containing configuration parameters for this ipc service.
     * @return if parsing was successful.
     */
    public boolean extractSpecificConfig(JsonNode node) {
        String portString = node.getNumberValue("port");
        int port = Integer.parseInt(portString);
        setPort(port);
        return true;
    }

    protected abstract void stop();

    public final boolean setPort(int newPort) {
        if (newPort >= 1024 && newPort < 65535 && portAvailable(newPort)) {
            port = newPort;
            return true;
        }
        return false;
    }

    public final int getPort() {
        return port;
    }

    public abstract String getName();
}
