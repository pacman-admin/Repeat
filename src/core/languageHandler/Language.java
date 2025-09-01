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
package core.languageHandler;

public enum Language {
    JAVA("java"),
    MANUAL_BUILD("manual"),
    ;

    private final String text;

    /**
     * @param text
     */
    Language(final String text) {
        this.text = text;
    }

    public static Language identify(int index) {
        Language[] languages = Language.values();
        if (index < 0 || index >= languages.length) {
            return null;
        }
        return languages[index];
    }

    public static Language identify(String name) {
        for (Language language : Language.values()) {
            if (name.equals(language.toString())) {
                return language;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return text;
    }
}
