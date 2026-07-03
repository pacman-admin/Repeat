package core.config;

import argo.jdom.JsonRootNode;

public interface Parser {
    void parse(JsonRootNode from, Config to);
}