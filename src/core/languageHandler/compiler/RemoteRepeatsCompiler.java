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
package core.languageHandler.compiler;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import core.config.AbstractRemoteRepeatsClientsConfig;
import core.ipc.repeatClient.repeatPeerClient.RepeatsPeerServiceClient;
import core.ipc.repeatClient.repeatPeerClient.RepeatsPeerServiceClientManager;
import core.ipc.repeatClient.repeatPeerClient.api.RepeatsActionsApi.RepeatsRemoteCompilationHints;
import core.languageHandler.Language;
import core.userDefinedTask.AggregateUserDefinedAction;
import core.userDefinedTask.UserDefinedAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RemoteRepeatsCompiler extends AbstractCompiler {

    private static final Logger LOGGER = Logger.getLogger(RemoteRepeatsCompiler.class.getName());

    private RemoteRepeatsCompilerConfig config;
    private RepeatsPeerServiceClientManager clients;
    // Used when compiling as a hint.
    private Map<String, String> clientIdToActionId;

    public RemoteRepeatsCompiler(RemoteRepeatsCompilerConfig config, RepeatsPeerServiceClientManager clients) {
        this.config = config;
        this.clients = clients;
    }

    public RemoteRepeatsCompiler cloneWithConfig(RemoteRepeatsCompilerConfig config) {
        return new RemoteRepeatsCompiler(config, clients);
    }

    public void setRemoteCompilationInfo(Map<String, String> clientIdToActionId) {
        this.clientIdToActionId = clientIdToActionId;
    }

    @Override
    public RemoteRepeatsDyanmicCompilationResult compile(String source, Language language) {
        List<Thread> executions = new ArrayList<>();
        Lock mutex = new ReentrantLock(true);
        List<UserDefinedAction> actions = new ArrayList<>();

        Map<String, String> compilationHint = clientIdToActionId != null ? clientIdToActionId : new HashMap<>();
        Map<String, String> compilationInfo = new HashMap<>();

        for (String clientId : config.getClients()) {
            if (clientId.equals(AbstractRemoteRepeatsClientsConfig.LOCAL_CLIENT)) {
                continue;
            }

            executions.add(new Thread(() -> {
                RepeatsPeerServiceClient client = clients.getClient(clientId);
                if (client == null) {
                    LOGGER.warning("Cannot compile action with client " + clientId + " since it does not exist.");
                    return;
                }

                String previousId = compilationHint.getOrDefault(client.getClientId(), "");
                UserDefinedAction action = client.api().actions().createTask(source, language, RepeatsRemoteCompilationHints.of(previousId));
                if (action == null) {
                    LOGGER.warning("Failed to compile remote Repeats action.");
                    return;
                }

                mutex.lock();
                try {
                    actions.add(action);
                    compilationInfo.put(clientId, action.getActionId());
                } finally {
                    mutex.unlock();
                }
            }));
        }

        for (Thread t : executions) {
            t.start();
        }
        for (Thread t : executions) {
            try {
                t.join();
            } catch (InterruptedException e) {
                LOGGER.log(Level.WARNING, "Interrupted while waiting for compilaiton thread.", e);
            }
        }
        if (actions.size() != executions.size()) {
            return RemoteRepeatsDyanmicCompilationResult.of(DynamicCompilerOutput.COMPILATION_ERROR, null, null);
        }

        return RemoteRepeatsDyanmicCompilationResult.of(DynamicCompilerOutput.COMPILATION_SUCCESS, AggregateUserDefinedAction.of(actions), compilationInfo);
    }

    @Override
    public boolean parseCompilerSpecificArgs(JsonNode node) {
        return true;
    }

    @Override
    public JsonNode getCompilerSpecificArgs() {
        return JsonNodeFactories.object();
    }

    @Override
    public Logger getLogger() {
        return Logger.getLogger(RemoteRepeatsCompiler.class.getName());
    }
}
