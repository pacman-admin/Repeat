package staticResources;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import core.config.Config;
import core.languageHandler.Language;
import utilities.FileUtility;

public class BootStrapResources {

	private static final Logger LOGGER = Logger.getLogger(BootStrapResources.class.getName());

	private static final Map<Language, String> LANGUAGE_API;
	private static final Map<Language, String> NATIVE_LANGUAGE_TEMPLATES;
	private static final Map<Language, AbstractNativeLanguageBootstrapResource> NATIVE_BOOTSTRAP_RESOURCES;
	private static final Set<BootstrapResourcesExtrator> BOOTSTRAP_RESOURCES;

	public static final String SOUND_POSITIVE1_PATH = "/staticContent/sounds/notifications/positive1.wav";
	public static final String SOUND_POSITIVE2_PATH = "/staticContent/sounds/notifications/positive2.wav";
	public static final String SOUND_POSITIVE3_PATH = "/staticContent/sounds/notifications/positive3.wav";
	public static final String SOUND_POSITIVE4_PATH = "/staticContent/sounds/notifications/positive4.wav";
	public static final String SOUND_NEGATIVE1_PATH = "/staticContent/sounds/notifications/negative1.wav";
	public static final String SOUND_NEGATIVE2_PATH = "/staticContent/sounds/notifications/negative2.wav";


	private static final NativeHookBootstrapResources nativeHookResources;

	public static final Image TRAY_IMAGE;

	static {
		TRAY_IMAGE = getImage("/staticContent/Repeat.jpg");

		/*********************************************************************************/
		LANGUAGE_API = new HashMap<>();
		LANGUAGE_API.put(Language.JAVA, getFile("/staticContent/core/languageHandler/API/JavaAPI.txt"));
		//LANGUAGE_API.put(Language.PYTHON, getFile("/staticContent/core/languageHandler/API/PythonAPI.txt"));
		LANGUAGE_API.put(Language.CSHARP, getFile("/staticContent/core/languageHandler/API/CSharpAPI.txt"));

		/*********************************************************************************/
		NATIVE_LANGUAGE_TEMPLATES = new HashMap<>();
		NATIVE_LANGUAGE_TEMPLATES.put(Language.JAVA, getFile("/staticContent/natives/java/TemplateRepeat"));
		//NATIVE_LANGUAGE_TEMPLATES.put(Language.PYTHON, getFile("/staticContent/natives/python/template_repeat.py"));
		NATIVE_LANGUAGE_TEMPLATES.put(Language.CSHARP, getFile("/staticContent/natives/csharp/source/TemplateRepeat.cs"));
		NATIVE_LANGUAGE_TEMPLATES.put(Language.MANUAL_BUILD, getFile("/staticContent/natives/manual/TemplateRepeat.txt"));

		/*********************************************************************************/
		NATIVE_BOOTSTRAP_RESOURCES = new HashMap<>();
		//NATIVE_BOOTSTRAP_RESOURCES.put(Language.PYTHON, new PythonResources());
		//NATIVE_BOOTSTRAP_RESOURCES.put(Language.CSHARP, new CSharpResources());

		/*********************************************************************************/
		BOOTSTRAP_RESOURCES = new HashSet<>();
		BOOTSTRAP_RESOURCES.addAll(NATIVE_BOOTSTRAP_RESOURCES.values());

		nativeHookResources = new NativeHookBootstrapResources();
		BOOTSTRAP_RESOURCES.add(nativeHookResources);
	}

	public static AbstractNativeLanguageBootstrapResource getBootstrapResource(Language language) {
		return NATIVE_BOOTSTRAP_RESOURCES.get(language);
	}

	public static void extractResources() throws IOException, URISyntaxException {
		for (BootstrapResourcesExtrator resource : BOOTSTRAP_RESOURCES) {
			resource.extractResources();
		}
	}

	protected static ImageIcon getIcon(String resource) {
		return new ImageIcon(getImage(resource));
	}

	public static InputStream getStaticContentStream(String resource) {
		return BootStrapResources.class.getResourceAsStream(resource);
	}

	protected static Image getImage(String resource) {
		try {
			return ImageIO.read(BootStrapResources.class.getResourceAsStream(resource));
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Cannot load image " + resource, e);
			return null;
		}
	}

	protected static String getFile(String path) {
		return FileUtility.readFromStream(BootStrapResources.class.getResourceAsStream(path)).toString();
	}

	public static String getAbout() {
		return "Repeat " + Config.RELEASE_VERSION + "\n"
				+ "A tool to repeat yourself with some intelligence.\n"
				+ "Created by HP Truong. Contact me at hptruong93@gmail.com.";
	}

	public static String getAPI(Language language) {
		if (LANGUAGE_API.containsKey(language)) {
			return LANGUAGE_API.get(language);
		} else {
			return "";
		}
	}

	public static String getNativeLanguageTemplate(Language language) {
		if (NATIVE_LANGUAGE_TEMPLATES.containsKey(language)) {
			return NATIVE_LANGUAGE_TEMPLATES.get(language);
		} else {
			return "";
		}
	}

	private BootStrapResources() {}
}
