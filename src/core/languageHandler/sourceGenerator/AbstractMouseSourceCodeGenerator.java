package core.languageHandler.sourceGenerator;

import java.util.Arrays;

public abstract class AbstractMouseSourceCodeGenerator extends AbstractDeviceSourceGenerator {

	@Override
	protected final boolean isKnownAction(String action) {
		return Arrays.asList("move", "moveBy", "press", "release", "click").contains(action);
	}

	@Override
	protected final String internalGetSourceCode(String action, int[] params) {
        return switch (action) {
            case "move" -> move(params);
            case "moveBy" -> moveBy(params);
            case "click" -> click(params);
            case "press" -> press(params);
            case "release" -> release(params);
            default -> null;
        };
	}

	/**
	 * Source code to move mouse to a position.
	 * @param params parameters to generate the source code.
	 * @return source code for this action.
	 */
	protected abstract String move(int[] params);

	/**
	 * Source code to move mouse by to a certain position.
	 * @param params parameters to generate the source code.
	 * @return source code for this action.
	 */
	protected abstract String moveBy(int[] params);

	/**
	 * Source code to click a mask of the mouse.
	 * @param params parameters to generate the source code.
	 * @return source code for this action.
	 */
	protected abstract String click(int[] params);

	/**
	 * Source code to press a mask of the mouse.
	 * @param params parameters to generate the source code.
	 * @return source code for this action.
	 */
	protected abstract String press(int[] params);

	/**
	 * Source code to release a mask of the mouse.
	 * @param params parameters to generate the source code.
	 * @return source code for this action.
	 */
	protected abstract String release(int[] params);
}
