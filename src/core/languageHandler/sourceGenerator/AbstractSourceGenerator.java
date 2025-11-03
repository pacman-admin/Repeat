package core.languageHandler.sourceGenerator;

import core.languageHandler.Language;
import core.scheduler.SchedulingData;
import staticResources.BootStrapResources;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public abstract class AbstractSourceGenerator {

    private static final Logger LOGGER = Logger.getLogger(AbstractSourceGenerator.class.getName());
    private static final String TAB = "    ";
    static final String TWO_TAB = TAB + TAB;
    private static final Map<Language, AbstractSourceGenerator> REFERENCE_SOURCES;

    static {
        REFERENCE_SOURCES = new HashMap<>();
        REFERENCE_SOURCES.put(Language.JAVA, new JavaSourceGenerator());
        //REFERENCE_SOURCES.put(Language.PYTHON, new PythonSourceGenerator());
        //REFERENCE_SOURCES.put(Language.CSHARP, new CSharpSourceGenerator());
        REFERENCE_SOURCES.put(Language.MANUAL_BUILD, new ManuallyBuildSourceGenerator());
    }

    private final StringBuffer source;
    private final AbstractKeyboardSourceCodeGenerator keyboardSourceCodeGenerator;
    private final AbstractMouseSourceCodeGenerator mouseSourceCodeGenerator;
    TaskSourceScheduler sourceScheduler;

    AbstractSourceGenerator() {
        source = new StringBuffer();
        sourceScheduler = new TaskSourceScheduler();

        keyboardSourceCodeGenerator = buildKeyboardSourceCodeGenerator();
        mouseSourceCodeGenerator = buildMouseSourceCodeGenerator();
    }

    public static String getReferenceSource(Language language) {
        AbstractSourceGenerator generator = REFERENCE_SOURCES.get(language);
        if (generator != null) {
            return generator.getSource(1); // No speedup
        }
        return null;
    }

    public final boolean submitTask(long time, Device device, String action, int[] param) {
        return verify(device, action, param) && internalSubmitTask(time, device, action, param);

    }

    private boolean internalSubmitTask(long time, Device device, String action, int[] params) {
        String mid = "";
        if (device.equals(Device.MOUSE)) {
            mid = mouseSourceCodeGenerator.getSourceCode(action, params);
        } else if (device.equals(Device.KEYBOARD)) {
            mid = keyboardSourceCodeGenerator.getSourceCode(action, params);
        } else {
            return false;
        }

        return mid != null && sourceScheduler.addTask(new SchedulingData<>(time, getSourceTab() + mid + "\n"));
    }

    private boolean verify(Object... ignored) {
        return true;
    }

    public final void clear() {
        source.setLength(0);
        sourceScheduler.clear();
    }

    /**
     * This method is called once in the constructor.
     *
     * @return an {@link AbstractMouseSourceCodeGenerator} to generate mouse source code.
     */
    protected abstract AbstractMouseSourceCodeGenerator buildMouseSourceCodeGenerator();

    /**
     * This method is called once in the constructor.
     *
     * @return an {@link AbstractMouseSourceCodeGenerator} to generate keyboard source code.
     */
    protected abstract AbstractKeyboardSourceCodeGenerator buildKeyboardSourceCodeGenerator();

    /**
     * @return the {@link Language} of generated source code.
     */
    protected abstract Language getSourceLanguage();

    /**
     * @return the amount of indentation required for the generated source code.
     */
    protected abstract String getSourceTab();

    public String getSource(float speedup) {
        String mainSource = sourceScheduler.getSource(speedup);
        if (mainSource == null) {
            LOGGER.severe("Unable to generate source...");
            mainSource = "";
        }

        return BootStrapResources.getNativeLanguageTemplate(getSourceLanguage()) +
                mainSource;
    }

    public enum Device {
        MOUSE("mouse"),
        KEYBOARD("keyBoard");

        private final String text;

        Device(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }
}
