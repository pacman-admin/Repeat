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
package core.ipc;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import utilities.ILoggable;

import java.io.IOException;

public abstract class IIPCService implements ILoggable {

    protected int port;
    private boolean launchAtStartup;

    protected IIPCService() {
        launchAtStartup = true;
    }

    public final void startRunning() throws IOException {
        if (!isRunning()) {
            start();
        } else {
            getLogger().info("This service is already running.");
        }
    }

    public final void stopRunning() throws IOException {
        if (!isRunning()) {
            return;
        }

        stop();
    }

    /**
     * Specific configuration parameters for this ipc service.
     *
     * @return the json node containing configuration parameters for this ipc service.
     */
    protected JsonNode getSpecificConfig() {
        return JsonNodeFactories.object(JsonNodeFactories.field("launch_at_startup", JsonNodeFactories.booleanNode(isLaunchAtStartup())));
    }

    /**
     * Extract internal configuration parameters for this ipc service.
     *
     * @param node the json node containing configuration parameters for this ipc service.
     * @return if parsing was successful.
     */
    protected boolean extractSpecificConfig(JsonNode node) {
        launchAtStartup = node.getBooleanValue("launch_at_startup");
        return true;
    }

    protected abstract void start() throws IOException;

    protected abstract void stop() throws IOException;

    public abstract boolean isRunning();

    public boolean setPort(int newPort) {
        if (isRunning()) {
            getLogger().warning("Cannot change port while running.");
            return false;
        }
        this.port = newPort;
        return true;
    }

    public final int getPort() {
        return port;
    }

    public abstract String getName();

    public boolean isLaunchAtStartup() {
        return launchAtStartup;
    }

    public void setLaunchAtStartup(boolean launchAtStartup) {
        this.launchAtStartup = launchAtStartup;
    }
}
