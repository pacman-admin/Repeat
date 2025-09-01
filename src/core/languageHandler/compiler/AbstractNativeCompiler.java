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

import core.languageHandler.Language;
import utilities.ILoggable;

import java.io.File;

public abstract class AbstractNativeCompiler extends AbstractCompiler implements ILoggable {

    @Override
    public final DynamicCompilationResult compile(String source, Language language) {
        if (language != getName()) {
            return DynamicCompilationResult.of(DynamicCompilerOutput.LANGUAGE_NOT_SUPPORTED, null);
        }
        return compile(source);
    }

    public abstract DynamicCompilationResult compile(String source);

    public abstract DynamicCompilationResult compile(String source, File objectFile);

    public abstract Language getName();

    public abstract String getExtension();

    public abstract String getObjectExtension();

    public abstract File getPath();

    public abstract boolean canSetPath();

    public abstract boolean setPath(File path);

    protected abstract File getSourceFile(String compilingAction);

    protected abstract String getDummyPrefix();
}
