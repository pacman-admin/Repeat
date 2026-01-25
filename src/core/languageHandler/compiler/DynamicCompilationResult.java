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
package core.languageHandler.compiler;

import core.userDefinedTask.UserDefinedAction;

public final class DynamicCompilationResult {
    private final DynamicCompilerOutput output;
    private final UserDefinedAction action;

    private DynamicCompilationResult(DynamicCompilerOutput output, UserDefinedAction action) {
        this.output = output;
        this.action = action;
    }

    public static DynamicCompilationResult of(DynamicCompilerOutput output, UserDefinedAction action) {
        return new DynamicCompilationResult(output, action);
    }

    public DynamicCompilerOutput output() {
        return output;
    }

    public UserDefinedAction action() {
        return action;
    }
}
