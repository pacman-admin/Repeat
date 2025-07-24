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

public class ClickerTool extends RepeatTool {

    @Override
    protected boolean isSupported(Language language) {
        return language == Language.JAVA;
    }

    @Override
    protected String getBodySource(Language language) {
        if (language == Language.JAVA) {

            return TWO_TAB + "for (int i = 0; ; i++) {\n" + THREE_TAB + "controller.mouse().leftClick();\n" + TWO_TAB + "}\n";
        }

        return null;
    }
}
