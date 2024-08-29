package staticResources;

import core.languageHandler.Language;
import utilities.FileUtility;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class BootStrapResources {

    /*public static final String SOUND_POSITIVE1_PATH = "/staticContent/sounds/notifications/positive1.wav";
    public static final String SOUND_POSITIVE2_PATH = "/staticContent/sounds/notifications/positive2.wav";
    public static final String SOUND_POSITIVE3_PATH = "/staticContent/sounds/notifications/positive3.wav";
    public static final String SOUND_POSITIVE4_PATH = "/staticContent/sounds/notifications/positive4.wav";
    public static final String SOUND_NEGATIVE1_PATH = "/staticContent/sounds/notifications/negative1.wav";
    public static final String SOUND_NEGATIVE2_PATH = "/staticContent/sounds/notifications/negative2.wav";*/
    public static final Image TRAY_IMAGE = getImage();
    private static final Logger LOGGER = Logger.getLogger(BootStrapResources.class.getName());
    private static final Map<Language, String> LANGUAGE_API;
    private static final Map<Language, String> NATIVE_LANGUAGE_TEMPLATES;
    private static final Map<Language, AbstractNativeLanguageBootstrapResource> NATIVE_BOOTSTRAP_RESOURCES;
    private static final Set<BootstrapResourcesExtractor> BOOTSTRAP_RESOURCES;
    private static final NativeHookBootstrapResources nativeHookResources;

    static {

        LANGUAGE_API = new HashMap<>();
        LANGUAGE_API.put(Language.JAVA, getFile("/staticContent/core/languageHandler/API/JavaAPI.txt"));
        LANGUAGE_API.put(Language.PYTHON, getFile("/staticContent/core/languageHandler/API/PythonAPI.txt"));
        LANGUAGE_API.put(Language.CSHARP, getFile("/staticContent/core/languageHandler/API/CSharpAPI.txt"));

        NATIVE_LANGUAGE_TEMPLATES = new HashMap<>();
        NATIVE_LANGUAGE_TEMPLATES.put(Language.JAVA, getFile("/staticContent/natives/java/TemplateRepeat"));
        NATIVE_LANGUAGE_TEMPLATES.put(Language.PYTHON, getFile("/staticContent/natives/python/template_repeat.py"));
        NATIVE_LANGUAGE_TEMPLATES.put(Language.CSHARP, getFile("/staticContent/natives/csharp/source/TemplateRepeat.cs"));
        NATIVE_LANGUAGE_TEMPLATES.put(Language.MANUAL_BUILD, getFile("/staticContent/natives/manual/TemplateRepeat.txt"));

        NATIVE_BOOTSTRAP_RESOURCES = new HashMap<>();
        NATIVE_BOOTSTRAP_RESOURCES.put(Language.PYTHON, new PythonResources());
        NATIVE_BOOTSTRAP_RESOURCES.put(Language.CSHARP, new CSharpResources());

        BOOTSTRAP_RESOURCES = new HashSet<>();
        BOOTSTRAP_RESOURCES.addAll(NATIVE_BOOTSTRAP_RESOURCES.values());

        nativeHookResources = new NativeHookBootstrapResources();
        BOOTSTRAP_RESOURCES.add(nativeHookResources);
    }

    private BootStrapResources() {
    }

    public static AbstractNativeLanguageBootstrapResource getBootstrapResource(Language language) {
        return NATIVE_BOOTSTRAP_RESOURCES.get(language);
    }

    public static void extractResources() throws IOException, URISyntaxException {
        for (BootstrapResourcesExtractor resource : BOOTSTRAP_RESOURCES) {
            resource.extractResources();
        }
    }

    /*private static ImageIcon getIcon(String resource) {
        return new ImageIcon(getImage(resource));
    }*/

    public static InputStream getStaticContentStream(String resource) {
        return BootStrapResources.class.getResourceAsStream(resource);
    }

    private static Image getImage() {
        try {
            //return ImageIO.read(BootStrapResources.class.getResourceAsStream(resource));
            return ImageIO.read(BootStrapResources.class.getResourceAsStream("/staticContent/Repeat.jpg"));
        } catch (IOException e) {
            assert LOGGER != null;
            LOGGER.log(Level.SEVERE, "Cannot load image " + "/staticContent/Repeat.jpg", e);
            return null;
        }
    }

    static String getFile(String path) {
        return FileUtility.readFromStream(BootStrapResources.class.getResourceAsStream(path)).toString();
    }

    /*public static String getAbout() {
        return "Repeat " + Config.RELEASE_VERSION + "\n" + "A tool to repeat yourself with some intelligence.\n" + "Created by HP Truong. Contact me at hptruong93@gmail.com.";
    }*/

    public static String getAPI(Language language) {
        return LANGUAGE_API.getOrDefault(language, "");
    }

    public static String getNativeLanguageTemplate(Language language) {
        return NATIVE_LANGUAGE_TEMPLATES.getOrDefault(language, "");
    }
}
