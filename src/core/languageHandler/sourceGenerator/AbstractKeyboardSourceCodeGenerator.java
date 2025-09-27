package core.languageHandler.sourceGenerator;

import java.util.Arrays;

public abstract class AbstractKeyboardSourceCodeGenerator extends AbstractDeviceSourceGenerator {

	@Override
	protected final boolean isKnownAction(String action) {
		return Arrays.asList("type", "press", "release").contains(action);
	}

	@Override
	protected final String internalGetSourceCode(String action, int[] params) {
        return switch (action) {
            case "type" -> type(params);
            case "press" -> press(params);
            case "release" -> release(params);
            default -> null;
        };
	}

	/**
	 * Source code to type keys.
	 * @param params parameters to generate the source code.
	 * @return source code for this action.
	 */
	protected abstract String type(int[] params);

	/**
	 * Source code to press a key.
	 * @param params parameters to generate the source code.
	 * @return source code for this action.
	 */
	protected abstract String press(int[] params);

	/**
	 * Source code to release a key.
	 * @param params parameters to generate the source code.
	 * @return source code for this action.
	 */
	protected abstract String release(int[] params);
}
