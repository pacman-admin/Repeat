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
import core.cli.server.CliRpcCodec;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparsers;
import utilities.HttpClient;
import utilities.json.IJsonable;
import utilities.json.JSONUtility;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class CliActionProcessor {

    private static final Logger LOGGER = Logger.getLogger(CliActionProcessor.class.getName());

    protected HttpClient httpClient;

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public abstract void addArguments(Subparsers parser);

    public abstract void handle(Namespace namespace);

    protected final void sendRequest(String path, IJsonable message) {
        byte[] data = CliRpcCodec.encode(JSONUtility.jsonToString(message.jsonize()).getBytes(CliRpcCodec.ENCODING));
        sendRequest(path, data);
    }

    private void sendRequest(String path, byte[] data) {
        try {
            byte[] responseData = httpClient.sendPost(path, data);
            String responseString = CliRpcCodec.decode(responseData);
            LOGGER.info(responseString);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Encountered IOException when talking to server.", e);
            CliExitCodes.IO_EXCEPTION.exit();
        }
    }
}
