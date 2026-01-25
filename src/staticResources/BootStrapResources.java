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
package staticResources;

import core.config.Constants;
import core.languageHandler.Language;
import utilities.FileUtility;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public final class BootStrapResources {
    public static final Image TRAY_IMAGE;
    private static final Logger LOGGER = Logger.getLogger(BootStrapResources.class.getName());
    private static final Map<Language, String> LANGUAGE_API;
    private static final Map<Language, String> NATIVE_LANGUAGE_TEMPLATES;
    private static final NativeHookBootstrapResources nativeHookResources;

    static {
        TRAY_IMAGE = getIcon();
        LANGUAGE_API = new HashMap<>();
        LANGUAGE_API.put(Language.JAVA, getFile("/staticContent/core/languageHandler/API/JavaAPI.txt"));
        NATIVE_LANGUAGE_TEMPLATES = new HashMap<>();
        NATIVE_LANGUAGE_TEMPLATES.put(Language.JAVA, getFile("/staticContent/natives/java/TemplateRepeat"));
        NATIVE_LANGUAGE_TEMPLATES.put(Language.MANUAL_BUILD, getFile("/staticContent/natives/manual/TemplateRepeat.txt"));
        nativeHookResources = new NativeHookBootstrapResources();
    }

    private BootStrapResources() {
        throw new InstantiationError("This class is uninstantiable.");
    }

    public static void extractResources() throws IOException, URISyntaxException {
        nativeHookResources.extractResources();
    }

    public static InputStream getStaticContentStream(String resource) {
        return BootStrapResources.class.getResourceAsStream(resource);
    }

    private static Image getIcon() {
        try {
            return ImageIO.read(Objects.requireNonNull(BootStrapResources.class.getResourceAsStream("/staticContent/Repeat.jpg")));
        } catch (IOException | NullPointerException e) {
            LOGGER.log(Level.SEVERE, "Cannot load application icon", e);
            return null;
        }
    }

    static String getFile(String path) {
        return FileUtility.readFromStream(BootStrapResources.class.getResourceAsStream(path)).toString();
    }

    public static String getAbout() {
        return "Repeat " + Constants.PROGRAM_VERSION + "\n" + "A tool to repeat yourself with some intelligence.\n" + "Created by HP Truong. Contact me at hptruong93@gmail.com.";
    }

    public static String getAPI(Language language) {
        return LANGUAGE_API.getOrDefault(language, "");
    }

    public static String getNativeLanguageTemplate(Language language) {
        return NATIVE_LANGUAGE_TEMPLATES.getOrDefault(language, "");
    }
}