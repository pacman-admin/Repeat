package globalListener;

import org.simplenativehooks.NativeHookInitializer;

public final class GlobalListenerHookController {

	private static final GlobalListenerHookController INSTANCE = new GlobalListenerHookController();

	private GlobalListenerHookController() {}

	public static final class Config {
		// Only applicable for Windows.
		private final boolean useJavaAwtForMousePosition;

		private Config(boolean useJavaAwtForMousePosition) {
			this.useJavaAwtForMousePosition = useJavaAwtForMousePosition;
		}

		boolean useJavaAwtForMousePosition() {
			return useJavaAwtForMousePosition;
		}

		public static final class Builder {
			private boolean useJavaAwtForMousePosition;

			public static Builder of() {
				return new Builder();
			}

			public Builder useJavaAwtForMousePosition(boolean use) {
				this.useJavaAwtForMousePosition = use;
				return this;
			}

			public Config build() {
				return new Config(useJavaAwtForMousePosition);
			}
		}
	}

	public static GlobalListenerHookController of() {
		return INSTANCE;
	}

	public void initialize(Config config) {
		NativeHookInitializer.Config nativeConfig = NativeHookInitializer.Config.Builder.of().useJnaForWindows(true)
				.useJavaAwtToReportMousePositionOnWindows(config.useJavaAwtForMousePosition())
				.build();
		NativeHookInitializer.of(nativeConfig).start();
	}

	public void cleanup() {
		NativeHookInitializer.of().stop();
	}
}
