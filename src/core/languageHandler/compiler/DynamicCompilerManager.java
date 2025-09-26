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
import argo.jdom.JsonRootNode;
import core.ipc.repeatClient.repeatPeerClient.RepeatsPeerServiceClientManager;
import core.languageHandler.Language;
import utilities.FileUtility;
import utilities.json.IJsonable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class DynamicCompilerManager implements IJsonable {
    private static final Logger LOGGER = Logger.getLogger(DynamicCompilerManager.class.getName());
    private final Map<Language, AbstractNativeCompiler> compilers;
    private RemoteRepeatsCompilerConfig remoteRepeatsCompilerConfig;

    public DynamicCompilerManager() {
        compilers = new HashMap<>();
        compilers.put(Language.JAVA, new JavaNativeCompiler("CustomAction", new String[]{"core"}, new String[]{}));
        compilers.put(Language.MANUAL_BUILD, new ManualBuildNativeCompiler(new File("core")));
        remoteRepeatsCompilerConfig = new RemoteRepeatsCompilerConfig(new ArrayList<>());
    }

    public AbstractNativeCompiler getNativeCompiler(Language name) {
        return compilers.get(name);
    }

    public AbstractNativeCompiler getNativeCompiler(String name) {
        return getNativeCompiler(Language.identify(name));
    }

    public boolean hasCompiler(Language name) {
        return compilers.containsKey(name);
    }

    public RemoteRepeatsCompilerConfig getRemoteRepeatsCompilerConfig() {
        return remoteRepeatsCompilerConfig;
    }

    @Override
    public JsonRootNode jsonize() {
        List<JsonNode> compilerList = new ArrayList<>();
        for (AbstractNativeCompiler compiler : compilers.values()) {
            compilerList.add(JsonNodeFactories.object(JsonNodeFactories.field("name", JsonNodeFactories.string(compiler.getName().toString())), JsonNodeFactories.field("path", JsonNodeFactories.string(FileUtility.getRelativePwdPath(compiler.getPath()))), JsonNodeFactories.field("compiler_specific_args", compiler.getCompilerSpecificArgs())));
        }

        return JsonNodeFactories.object(JsonNodeFactories.field("local_compilers", JsonNodeFactories.array(compilerList)), JsonNodeFactories.field("remote_repeats_compilers", remoteRepeatsCompilerConfig.jsonize()));
    }

    public boolean parseJSON(JsonNode compilerSettings) {
        JsonNode remoteRepeatsCompilers = compilerSettings.getNode("remote_repeats_compilers");
        RemoteRepeatsCompilerConfig remoteCompilers = RemoteRepeatsCompilerConfig.parseJSON(remoteRepeatsCompilers);
        remoteRepeatsCompilerConfig.setClients(remoteCompilers.getClients());

        List<JsonNode> localCompilers = compilerSettings.getArrayNode("local_compilers");
        for (JsonNode compilerNode : localCompilers) {
            String name = compilerNode.getStringValue("name");
            String path = compilerNode.getStringValue("path");
            JsonNode compilerSpecificArgs = compilerNode.getNode("compiler_specific_args");
            //JsonNode compilerSpecificArgs = compilerNode.getNode("compilers");
            AbstractNativeCompiler compiler = getNativeCompiler(name);
            if (compiler != null) {
                compiler.setPath(new File(path));
                if (!compiler.parseCompilerSpecificArgs(compilerSpecificArgs)) {
                    LOGGER.warning("Compiler " + name + " was unable to parse its specific arguments.\n");
                }
            } else {
                throw new IllegalStateException("Unknown compiler " + name);
            }
        }
        return true;
    }
}
