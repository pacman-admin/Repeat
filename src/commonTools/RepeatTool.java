/**
 * Copyright 2025 Langdon Staab
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
package commonTools;

import core.languageHandler.Language;
import utilities.StringUtilities;

import java.util.LinkedList;
import java.util.List;

public abstract class RepeatTool {

    protected static final String TAB = "    ";
    protected static final String TWO_TAB = TAB + TAB;
    protected static final String THREE_TAB = TWO_TAB + TAB;
    protected static final String FOUR_TAB = THREE_TAB + TAB;

    protected List<String> imports;

    public RepeatTool() {
        imports = new LinkedList<>();
        imports.add("import core.UserDefinedAction;");
        imports.add("import core.controller.Core;");
    }

    public String getSource(Language language) {
        if (isSupported(language)) {
            return "package core;\n" + StringUtilities.join(imports, "\n") + "\n\n" + getHeader(language) + getBodySource(language) + getFooter(language);
        } else {
            return "";
        }
    }

    protected String getHeader(Language language) {
        if (language == Language.JAVA) {
            return """
                    public class CustomAction extends UserDefinedAction {
                        public void action(final Core controller) throws InterruptedException {
                    """;
        } else {
            return "";
        }
    }

    protected String getFooter(Language language) {
        if (language == Language.JAVA) {
            return "    }\n}";
        } else {
            return "";
        }
    }

    protected abstract boolean isSupported(Language language);

    protected abstract String getBodySource(Language language);
}
