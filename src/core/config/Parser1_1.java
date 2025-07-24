package core.config;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;

public class Parser1_1 extends ConfigParser {

	private static final Logger LOGGER = Logger.getLogger(Parser1_1.class.getName());

	@Override
	protected String getVersion() {
		return "1.1";
	}

	@Override
	protected String getPreviousVersion() {
		return "1.0";
	}

	@Override
	protected JsonRootNode internalConvertFromPreviousVersion(JsonRootNode previousVersion) {
		try {
			List<JsonNode> newTask = new ArrayList<>();
			for (JsonNode oldTask : previousVersion.getArrayNode("tasks")) {
				newTask.add(JsonNodeFactories.object(
							JsonNodeFactories.field("source_path", oldTask.getNode("source_path")),
							JsonNodeFactories.field("compiler", oldTask.getNode("compiler")),
							JsonNodeFactories.field("name", oldTask.getNode("name")),
							JsonNodeFactories.field("hotkey", oldTask.getNode("hotkey")),
							JsonNodeFactories.field("enabled", JsonNodeFactories.booleanNode(true))
						));
			}
			JsonNode onlyGroup = JsonNodeFactories.array(JsonNodeFactories.object(
					JsonNodeFactories.field("name", JsonNodeFactories.string("default")),
					JsonNodeFactories.field("enabled", JsonNodeFactories.booleanNode(true)),
					JsonNodeFactories.field("tasks", JsonNodeFactories.array(newTask))
					));


            return JsonNodeFactories.object(
                    JsonNodeFactories.field("version", JsonNodeFactories.string(getVersion())),
                    JsonNodeFactories.field("global_hotkey", previousVersion.getNode("global_hotkey")),
                    JsonNodeFactories.field("compilers", previousVersion.getNode("compilers")),
                    JsonNodeFactories.field("task_groups", onlyGroup)
                    );
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Unable to convert json from previous version " + getPreviousVersion(), e);
			return null;
		}
	}

	@Override
	protected boolean internalImportData(Config config, JsonRootNode data) {
		LOGGER.warning("Unsupported import data at version " + getVersion());
		return false;
	}
}