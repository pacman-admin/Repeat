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
package staticResources;

import core.languageHandler.Language;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Logger;

public abstract class AbstractNativeLanguageBootstrapResource extends AbstractBootstrapResource {

    private static final Logger LOGGER = Logger.getLogger(AbstractNativeLanguageBootstrapResource.class.getName());

    @Override
    public final void extractResources() throws IOException, URISyntaxException {
        super.extractResources();
        if (!generateKeyCode()) {
            LOGGER.warning("Unable to generate key code");
        }
    }

    @Override
    protected final String getName() {
        return getLanguage().name();
    }

    protected abstract Language getLanguage();

    protected abstract boolean generateKeyCode();

    public abstract File getIPCClient();
}
