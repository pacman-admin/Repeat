package globalListener;

import org.simplenativehooks.NativeHookInitializer;

public class GlobalListenerHookController {
    private GlobalListenerHookController() {}

    public static void initialize(boolean useAWTForMousePos) {
        NativeHookInitializer.Config.Builder confBuilder = NativeHookInitializer.Config.Builder.of();
        confBuilder.useJnaForWindows(true);
        confBuilder.useJavaAwtToReportMousePositionOnWindows(useAWTForMousePos);
        NativeHookInitializer.of(confBuilder.build()).start();
    }

    public static void cleanup() {
        NativeHookInitializer.of().stop();
    }
}