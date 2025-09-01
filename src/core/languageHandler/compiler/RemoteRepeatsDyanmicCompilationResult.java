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

import core.userDefinedTask.UserDefinedAction;

import java.util.Map;

public class RemoteRepeatsDyanmicCompilationResult extends DynamicCompilationResult {

    private Map<String, String> clientIdToActionId;

    private RemoteRepeatsDyanmicCompilationResult(DynamicCompilerOutput output, UserDefinedAction action, Map<String, String> clientIdToActionId) {
        super(output, action);
        this.clientIdToActionId = clientIdToActionId;
    }

    public static RemoteRepeatsDyanmicCompilationResult of(DynamicCompilerOutput output, UserDefinedAction action, Map<String, String> clientIdToActionId) {
        return new RemoteRepeatsDyanmicCompilationResult(output, action, clientIdToActionId);
    }

    public Map<String, String> clientIdToActionId() {
        return clientIdToActionId;
    }
}
