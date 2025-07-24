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
package core.config;

import java.util.logging.Logger;

import argo.jdom.JsonRootNode;

public class Parser1_0 extends ConfigParser {

	private static final Logger LOGGER = Logger.getLogger(Parser1_0.class.getName());

	@Override
	protected String getVersion() {
		return "1.0";
	}

	@Override
	protected String getPreviousVersion() {
		return null;
	}

	@Override
	protected JsonRootNode internalConvertFromPreviousVersion(JsonRootNode previousVersion) {
		return previousVersion;
	}

	@Override
	protected boolean internalImportData(Config config, JsonRootNode data) {
		LOGGER.warning("Unsupported import data at version " + getVersion());
		return false;
	}
}