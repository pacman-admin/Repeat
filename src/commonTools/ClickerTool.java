package commonTools;

import core.languageHandler.Language;

public final class ClickerTool extends RepeatTool {

	@Override
	protected boolean isSupported(Language language) {
		return language == Language.JAVA;
	}

	@Override
	protected String getBodySource(Language language) {
		if (language == Language.JAVA) {
			String output = TWO_TAB + "for (int i = 0; ; i++) {\n" +
					THREE_TAB + "controller.mouse().leftClick();\n" +
					TWO_TAB + "}\n";

			return output;
		}

		return null;
	}
}
