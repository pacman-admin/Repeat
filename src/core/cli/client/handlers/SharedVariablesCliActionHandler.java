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
package core.cli.client.handlers;

import core.cli.CliExitCodes;
import core.cli.messages.SharedVariablesGetMessage;
import core.cli.messages.SharedVariablesSetMessage;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SharedVariablesCliActionHandler extends CliActionProcessor {

    private static final Logger LOGGER = Logger.getLogger(SharedVariablesCliActionHandler.class.getName());

    @Override
    public void addArguments(Subparsers subparsers) {
        Subparser parser = subparsers.addParser("variable").setDefault("module", "variable").help("Shared variables management.");

        parser.addArgument("-a", "--action").required(true).choices("get", "set").help("Specify action on variable.");
        parser.addArgument("-s", "--namespace").setDefault("").help("Namespace in which this variable lives.");
        parser.addArgument("-n", "--variable").setDefault("").help("Name of the variable.");
        parser.addArgument("-v", "--value").setDefault("").help("Value of this variable (only used in set action.");
    }

    @Override
    public void handle(Namespace namespace) {
        String action = namespace.getString("action");
        if (action.equals("get")) {
            handleGet(namespace);
        } else if (action.equals("set")) {
            handleSet(namespace);
        } else {
            LOGGER.log(Level.SEVERE, "Unknown task action " + action);
            CliExitCodes.UNKNOWN_ACTION.exit();
        }
    }

    private void handleGet(Namespace namespace) {
        String space = namespace.getString("namespace");
        String variable = namespace.getString("variable");
        if (variable.isEmpty()) {
            LOGGER.warning("Variable name must be provided.");
            CliExitCodes.INVALID_ARGUMENTS.exit();
        }

        SharedVariablesGetMessage message = SharedVariablesGetMessage.of().setNamespace(space).setVariable(variable);
        sendRequest("/var/get", message);
    }

    private void handleSet(Namespace namespace) {
        String space = namespace.getString("namespace");
        String variable = namespace.getString("variable");
        if (variable.isEmpty()) {
            LOGGER.warning("Variable name must be provided.");
            CliExitCodes.INVALID_ARGUMENTS.exit();
        }
        String value = namespace.getString("value");
        if (value.isEmpty()) {
            LOGGER.warning("Variable value must be provided.");
            CliExitCodes.INVALID_ARGUMENTS.exit();
        }

        SharedVariablesSetMessage message = SharedVariablesSetMessage.of().setNamespace(space).setVariable(variable).setValue(value);
        sendRequest("/var/set", message);
    }
}
