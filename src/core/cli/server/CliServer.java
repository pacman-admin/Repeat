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
package core.cli.server;

import core.ipc.IPCServiceWithModifablePort;
import frontEnd.MainBackEndHolder;

import java.util.logging.Logger;

public class CliServer extends IPCServiceWithModifablePort {
    @Override
    protected void start() {
    }

    @Override
    protected void stop() {
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public String getName() {
        return "Pretend CLI Server";
    }

    @Override
    public Logger getLogger() {
        return Logger.getLogger(CliServer.class.getName());
    }

    public void setMainBackEndHolder(MainBackEndHolder ignored) {
    }
}
