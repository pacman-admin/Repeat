package utilities.json;

import argo.jdom.JsonRootNode;

public interface IJsonable {
	JsonRootNode jsonize();
}