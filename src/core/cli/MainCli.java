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
package core.cli;

import core.cli.client.handlers.CliActionProcessor;
import core.cli.client.handlers.SharedVariablesCliActionHandler;
import core.cli.client.handlers.TaskCliActionHandler;
import core.config.CliConfig;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparsers;
import utilities.HttpClient;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainCli {

    private static final Logger LOGGER = Logger.getLogger(MainCli.class.getName());

    private Map<String, CliActionProcessor> processors;

    public MainCli() {
        processors = new HashMap<>();
        processors.put("variable", new SharedVariablesCliActionHandler());
        processors.put("task", new TaskCliActionHandler());
    }

    private ArgumentParser setupParser() {
        ArgumentParser parser = ArgumentParsers.newFor("Repeat").build().defaultHelp(true).description("Execute Repeat operations in the terminal.");
        parser.addArgument("-s", "--host").type(String.class).setDefault("localhost").help("Specify a custom host at which the Repeat server is running.");
        parser.addArgument("-p", "--port").type(Integer.class).help("Specify a custom port at which the Repeat server is running." + "If not specified, port value is read from config file.");

        Subparsers subParsers = parser.addSubparsers().help("Help for each individual command.");
        for (CliActionProcessor processor : processors.values()) {
            processor.addArguments(subParsers);
        }

        return parser;
    }

    public void process(String[] args) {
        ArgumentParser parser = setupParser();
        Namespace namespace = null;
        try {
            namespace = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            CliExitCodes.INVALID_ARGUMENTS.exit();
        }
        CliConfig config = new CliConfig();
        config.loadConfig(null);
        // Override port if provided.
        Integer customPort = namespace.getInt("port");
        if (customPort != null) {
            config.setServerPort(customPort);
        }

        String serverAddress = String.format("%s:%s", namespace.getString("host"), config.getServerPort());
        HttpClient client = new HttpClient(serverAddress, HttpClient.Config.of());
        for (CliActionProcessor processor : processors.values()) {
            processor.setHttpClient(client);
        }

        String action = namespace.get("module");
        CliActionProcessor processor = processors.get(action);
        if (processor == null) {
            LOGGER.log(Level.SEVERE, "Unknown action " + action);
            CliExitCodes.UNKNOWN_ACTION.exit();
        }
        processor.handle(namespace);
    }
}
