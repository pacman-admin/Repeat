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

import core.languageHandler.Language;
import core.userDefinedTask.DormantUserDefinedTask;
import core.userDefinedTask.UserDefinedAction;
import utilities.FileUtility;
import utilities.RandomUtil;
import utilities.StringUtil;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class JavaNativeCompiler implements Compiler {

    private final String[] packageTree;
    private final String defaultClassName;
    private final DynamicClassLoader classLoader;
    private String className;

    public JavaNativeCompiler(String className, String[] packageTree) {
        this.packageTree = packageTree;
        this.defaultClassName = className;
        classLoader = new DynamicClassLoader();
    }

    @Override
    public CompilationResult compile(String sourceCode, File classFile) {
        className = FileUtility.removeExtension(classFile).getName();

        if (!classFile.getParentFile().getAbsolutePath().equals(new File(FileUtility.joinPath(packageTree)).getAbsolutePath())) {
            getLogger().warning("Class file " + classFile.getAbsolutePath() + "is not consistent with packageTree");
        } else if (!classFile.getName().endsWith(".class")) {
            getLogger().fine("Java class file " + classFile.getAbsolutePath() + " does not end with .class. Compiling using source code");
            return compile(sourceCode);
        } else if (!FileUtility.fileExists(classFile)) {
            getLogger().fine("Cannot find file " + classFile.getAbsolutePath() + ". Compiling using source code");
            return compile(sourceCode);
        }

        try {
            CompilationResult output = loadClass(className);
            getLogger().info("Skipped compilation and loaded object file.");
            className = null;
            return output;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IOException e) {
            getLogger().log(Level.WARNING, "Cannot load class file " + classFile.getAbsolutePath(), e);
            getLogger().info("Compiling using source code");
            return compile(sourceCode);
        } catch (Throwable e) {
            // Note that we need to catch Throwable instead of Exception
            // because certain class loading errors manifest as java.lang.Error, not java.lang.Exception.
            // As a result, catching Exception alone would not cover all
            // failure cases here.
            getLogger().log(Level.WARNING, "Encountering unknown throwable when loading class file " + classFile.getAbsolutePath(), e);
            getLogger().info("Compiling using source code");
            return compile(sourceCode);
        }
    }

    @Override
    public CompilationResult compile(String sourceCode) {
        if (!sourceCode.contains("class " + defaultClassName)) {
            getLogger().warning("Cannot find class " + defaultClassName + " in source code.");
            return CompilationResult.of(CompilationOutcome.SOURCE_MISSING_PREFORMAT_ELEMENTS);
        }

        String newClassName = className;
        if (newClassName == null) {
            newClassName = getDummyPrefix() + RandomUtil.randomID();
        }
        sourceCode = sourceCode.replaceFirst("class " + defaultClassName, "class " + newClassName);

        try {
            File compiling = getSourceFile(newClassName);
            if (compiling.getParentFile().exists() || compiling.getParentFile().mkdirs()) {
                try {
                    if (!FileUtility.writeToFile(sourceCode, compiling, false)) {
                        getLogger().warning("Cannot write source code to file.");
                        return CompilationResult.of(CompilationOutcome.SOURCE_NOT_ACCESSIBLE, new DormantUserDefinedTask(sourceCode, Language.JAVA));
                    }

                    /** Compilation Requirements *********************************************************************************************/
                    DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
                    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
                    if (compiler == null) {
                        getLogger().warning("No java compiler found. Compiling Java custom actions requires execution from a JDK; a JRE is not sufficient.");
                        return CompilationResult.of(CompilationOutcome.COMPILER_MISSING, new DormantUserDefinedTask(sourceCode, Language.JAVA));
                    }
                    StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, Locale.US, StandardCharsets.UTF_8);

                    // This sets up the class path that the compiler will use.
                    // Added the .jar file that contains the [className] interface within in it...
                    List<String> optionList = new ArrayList<>();
                    optionList.add("-classpath");
                    String paths = System.getProperty("java.class.path");
//                    if (classPaths.length > 0) {
//                        paths += ";" + StringUtil.join(classPaths, ";");
//                    }
                    optionList.add(paths);

                    Iterable<? extends JavaFileObject> compilationUnit = fileManager.getJavaFileObjectsFromFiles(List.of(compiling));
                    JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, optionList, null, compilationUnit);
                    /********************************************************************************************* Compilation Requirements **/
                    if (task.call()) {
                        CompilationResult output = loadClass(newClassName);
                        getLogger().info("Successfully compiled class " + defaultClassName);
                        return output;
                    } else {
                        for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                            String lineNumber = diagnostic != null ? String.valueOf(diagnostic.getLineNumber()) : "'unknown'";
                            String fileUri = "unknown";
                            if (diagnostic != null && diagnostic.getSource() != null) {
                                fileUri = diagnostic.getSource().toUri().toString();
                            }
                            String message = diagnostic != null ? diagnostic.getMessage(Locale.US) : "unknown message";
                            getLogger().warning("Error on line " + lineNumber + " in " + fileUri + ".");
                            getLogger().warning(message);
                        }
                    }
                    fileManager.close();
                } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException exp) {
                    getLogger().log(Level.WARNING, "Error during compilation...", exp);
                }
            }
            getLogger().warning("Cannot compile class " + defaultClassName);
            return CompilationResult.of(CompilationOutcome.COMPILATION_ERROR);
        } finally {
            className = null;
//            System.setProperty("java.home", originalPath);
        }
    }

    private CompilationResult loadClass(String loadClassName) throws ClassNotFoundException, InstantiationException, IllegalAccessException, MalformedURLException {
        classLoader.addURL(new File("./").toURI().toURL());
        Class<?> loadedClass = classLoader.loadClass(StringUtil.join(packageTree, ".") + "." + loadClassName);
        Object object;
        try {
            object = loadedClass.getDeclaredConstructor().newInstance();
        } catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            getLogger().log(Level.WARNING, "Unable to create a new instance...", e);
            return CompilationResult.of(CompilationOutcome.CONSTRUCTOR_ERROR);
        }

        getLogger().log(Level.FINE, "Successfully loaded class " + loadClassName);
        UserDefinedAction output = (UserDefinedAction) object;
        output.setSourcePath(getSourceFile(loadClassName).getAbsolutePath());
        return CompilationResult.of(CompilationOutcome.COMPILATION_SUCCESS, output);
    }

    @Override
    public File getSourceFile(String compileClass) {
        return new File(FileUtility.joinPath(FileUtility.joinPath(packageTree), compileClass + ".java"));
    }

    @Override
    public Language getName() {
        return Language.JAVA;
    }

    @Override
    public String getExtension() {
        return ".java";
    }

    @Override
    public String getObjectExtension() {
        return ".class";
    }

    @Override
    public String getDummyPrefix() {
        return "CC_";
    }

    @Override
    public Logger getLogger() {
        return Logger.getLogger(JavaNativeCompiler.class.getName());
    }
    private static final class DynamicClassLoader extends URLClassLoader {
        private DynamicClassLoader() {
            super(new URL[0], ClassLoader.getSystemClassLoader());
        }

        /**
         * Note that adding a path multiple times is fine since underlying
         * implementation treats it as no-op.
         */
        @Override
        protected void addURL(URL url) {
            super.addURL(url);
        }
    }
}
