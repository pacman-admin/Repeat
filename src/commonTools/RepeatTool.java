package commonTools;

import java.util.LinkedList;
import java.util.List;

import utilities.StringUtilities;
import core.languageHandler.Language;

abstract class RepeatTool {

	private static final String TAB = "    ";
	static final String TWO_TAB = TAB + TAB;
	static final String THREE_TAB = TWO_TAB + TAB;
	static final String FOUR_TAB = THREE_TAB + TAB;

	final List<String> imports;

	RepeatTool() {
		imports = new LinkedList<>();
		imports.add("import core.UserDefinedAction;");
		imports.add("import core.controller.Core;");
	}

	public String getSource(Language language) {
        return isSupported(language) ? "package core;\n"
                + StringUtilities.join(imports, "\n") + "\n\n"
                + getHeader(language) + getBodySource(language) + getFooter(language) : "";
	}

	private String getHeader(Language language) {
        return language == Language.JAVA ? "public class CustomAction extends UserDefinedAction {\n"
                + "    public void action(final Core controller) throws InterruptedException {\n" : "";
	}

	private String getFooter(Language language) {
        return language == Language.JAVA ? "    }\n}" : "";
	}

	protected abstract boolean isSupported(Language language);
	protected abstract String getBodySource(Language language);
}
