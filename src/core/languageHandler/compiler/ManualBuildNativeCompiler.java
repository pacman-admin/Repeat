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

import argo.jdom.JsonNode;
import core.languageHandler.Language;
import core.userDefinedTask.manualBuild.ManuallyBuildAction;
import core.userDefinedTask.manualBuild.ManuallyBuildActionConstructor;
import core.userDefinedTask.manualBuild.ManuallyBuildStep;
import org.simplenativehooks.utilities.FileUtility;
import utilities.RandomUtil;
import utilities.json.JSONUtility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public final class ManualBuildNativeCompiler implements Compiler {

    public static final String VERSION = "1.0";
    public static final String VERSION_PREFIX = "version=";
    private static final Logger LOGGER = Logger.getLogger(ManualBuildNativeCompiler.class.getName());
    private final File tempSourceDir;

    public ManualBuildNativeCompiler(File tempSourceDir) {
        this.tempSourceDir = tempSourceDir;
    }

    /**
     * Start with a simple compiler:
     * <p>
     * If a line is empty starts with "//", ignore it.
     * The first parsable line must be "version=xxx".
     * Every other line is a complete JSON object specifying a step.
     * <p>
     * See {@link ManuallyBuildActionConstructor#generateSource()} for JSON format.
     */
    @Override
    public CompilationResult compile(String source) {
        //LOGGER.log(Level.INFO,"Bla",new RuntimeException("compiler called"));
        String[] lines = source.split("\n");
        boolean foundVersion = false;

        List<ManuallyBuildStep> steps = new ArrayList<>(lines.length);
        for (String line : lines) {
            String trimmed = line.trim();

            // If a line is empty or starts with "//", ignore it.
            if (trimmed.isBlank() || trimmed.startsWith("//")) {
                continue;
            }

            if (!foundVersion) {
                if (!trimmed.startsWith(VERSION_PREFIX)) {
                    LOGGER.warning("First parsable line must start with '" + VERSION_PREFIX + "' but got " + trimmed);
                    return CompilationResult.of(CompilationOutcome.COMPILATION_ERROR);
                }

                foundVersion = true;
                continue;
            }

            JsonNode node = JSONUtility.jsonFromString(trimmed);
            if (node == null) {
                LOGGER.warning("Cannot parse JSON string " + trimmed + ".");
                return CompilationResult.of(CompilationOutcome.SOURCE_NOT_ACCESSIBLE);
            }

            ManuallyBuildStep s = ManuallyBuildStep.parseJSON(node);
            if (s == null) {
                getLogger().warning("Unable to parse step from JSON " + JSONUtility.jsonToString(node));
                return CompilationResult.of(CompilationOutcome.COMPILATION_ERROR);
            }
            steps.add(s);
        }

        ManuallyBuildAction action = ManuallyBuildAction.of(steps);
        File sourceFile = getSourceFile(RandomUtil.randomID());
        if (!FileUtility.writeToFile(source, sourceFile, false)) {
            LOGGER.warning("Cannot write source code to file.");
            return CompilationResult.of(CompilationOutcome.SOURCE_NOT_ACCESSIBLE);
        }

        action.setSourcePath(sourceFile.getAbsolutePath());
        LOGGER.info("Successfully compiled custom action.");
        return CompilationResult.of(CompilationOutcome.COMPILATION_SUCCESS, action);
    }

    @Override
    public CompilationResult compile(String source, File objectFile) {
        return compile(source);
    }

    @Override
    public Language getName() {
        return Language.MANUAL_BUILD;
    }

    @Override
    public String getExtension() {
        return ".manualbuild";
    }

    @Override
    public String getObjectExtension() {
        return ".manualbuildobj";
    }

    @Override
    public File getSourceFile(String compilingAction) {
        return new File(FileUtility.joinPath(tempSourceDir.getAbsolutePath(), getDummyPrefix() + compilingAction + getExtension()));
    }

    @Override
    public String getDummyPrefix() {
        return "MANUAL_";
    }

    @Override
    public Logger getLogger() {
        return Logger.getLogger(ManualBuildNativeCompiler.class.getName());
    }
}
