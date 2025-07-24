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
package core.cli.server;

import argo.jdom.JsonNode;
import core.webui.webcommon.HttpServerUtilities;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import utilities.json.JSONUtility;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class CliRpcCodec {

    public static final Charset ENCODING = StandardCharsets.UTF_8;

    public static byte[] encode(byte[] input) {
        return Base64.getEncoder().encode(input);
    }

    public static String decode(byte[] data) {
        return new String(Base64.getDecoder().decode(data), CliRpcCodec.ENCODING);
    }

    public static JsonNode decodeRequest(byte[] data) {
        return JSONUtility.jsonFromString(decode(data));
    }

    public static Void prepareResponse(HttpAsyncExchange exchange, int code, String data) throws IOException {
        return prepareResponse(exchange, code, data.getBytes(CliRpcCodec.ENCODING));
    }

    private static Void prepareResponse(HttpAsyncExchange exchange, int code, byte[] data) throws IOException {
        return HttpServerUtilities.prepareResponse(exchange, code, encode(data));
    }
}
