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
package core.webui.server.handlers;

import core.languageHandler.Language;
import staticResources.BootStrapResources;

public class ApiPageHandler extends AbstractGETHandler {

    public ApiPageHandler() {
        super("Could not get API documentation for selected language!");
    }

    @Override
    protected String handle() {
        Language selected = backEndHolder.getSelectedLanguage();
        if (selected == Language.MANUAL_BUILD) //return "The manual build compiler has no API.";
            throw new NullPointerException("The manual build compiler has no API.");
        return BootStrapResources.getAPI(selected);
    }
}