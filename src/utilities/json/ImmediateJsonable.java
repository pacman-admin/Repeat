package utilities.json;

import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;

public final class ImmediateJsonable implements IJsonable {

	private final JsonNode node;

	private ImmediateJsonable(JsonNode node) {
		this.node = node;
	}

	public static ImmediateJsonable of(JsonNode node) {
		return new ImmediateJsonable(node);
	}

	@Override
	public JsonRootNode jsonize() {
		return node.getRootNode();
	}
}
