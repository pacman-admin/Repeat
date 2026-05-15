package core.webui.webcommon;

import argo.jdom.JsonField;
import argo.jdom.JsonNode;
import com.sun.net.httpserver.HttpExchange;
import utilities.json.JSONUtility;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

public final class HttpServerUtilities {

    private static final Logger LOGGER = Logger.getLogger(HttpServerUtilities.class.getName());

    private HttpServerUtilities() {
        throw new InstantiationError("This class is uninstantiable.");
    }

    public static Map<String, String> parseGetParameters(URI url) throws UnsupportedEncodingException {
        String query = url.getQuery(); // Returns "q=java+parse&lang=en"

        Map<String, String> queryParams = new LinkedHashMap<>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            // Use URLDecoder to handle special characters and spaces
            String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
            String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
            queryParams.put(key, value);
        }

        System.out.println(queryParams);
        return queryParams;
    }

    public static JsonNode parsePostParameters(HttpExchange exchange) {
        byte[] content = getPostContent(exchange);
        if (content == null) {
            LOGGER.warning("Failed to get POST content.");
            return null;
        }

        return getPostParameters(content);
    }

    public static byte[] getPostContent(HttpExchange exchange) {
        try {
            byte[] data = exchange.getRequestBody().readAllBytes();
            exchange.getRequestBody().close();
            return data;
        } catch (IOException e) {
            LOGGER.warning("Error reading request data");
        }
        return null;
    }

    public static Map<String, String> parseSimplePostParameters(HttpExchange exchange) {
        byte[] content = getPostContent(exchange);
        if (content == null) {
            LOGGER.warning("Failed to get POST content.");
            return null;
        }

        return getSimplePostParameters(content);
    }

    private static JsonNode getPostParameters(byte[] content) {
        String postContent = new String(content, StandardCharsets.UTF_8);
        JsonNode node = JSONUtility.jsonFromString(postContent);
        if (node == null) {
            LOGGER.warning("Failed to parse content into JSON.");
        }
        return node;
    }

    private static Map<String, String> getSimplePostParameters(byte[] content) {
        Map<String, String> output = new HashMap<>();
        JsonNode node = getPostParameters(content);
        if (node == null) {
            return null;
        }

        for (JsonField field : node.getFieldList()) {
            String name = field.getName().getStringValue();
            JsonNode valueNode = field.getValue();
            if (!valueNode.isStringValue() && !valueNode.isNumberValue()) {
                LOGGER.warning("Value not is not a string node.");
                return null;
            }
            String value;
            if (valueNode.isStringValue()) {
                value = valueNode.getStringValue();
            } else if (valueNode.isNumberValue()) {
                value = valueNode.getNumberValue();
            } else if (valueNode.isBooleanValue()) {
                value = valueNode.getBooleanValue() + "";
            } else {
                LOGGER.warning("Value is not a string or number node.");
                return null;
            }
            output.put(name, value);
        }

        return output;
    }

    public static void prepareHttpResponse(HttpExchange exchange, int code, String data) {
        prepareStringResponse(exchange, code, data, "text/html");
    }

    public static void prepareTextResponse(HttpExchange exchange, int code, String data) {
        prepareStringResponse(exchange, code, data, "text/plain; charset=utf-8");
    }

    public static void prepareJsonResponse(HttpExchange exchange, int code, JsonNode data) {
        prepareStringResponse(exchange, code, JSONUtility.jsonToSingleLineString(data), "application/json; charset=utf-8");
    }

    private static void prepareStringResponse(HttpExchange exchange, int code, String data, String contentType) {
        try {
            exchange.sendResponseHeaders(code, data.length());
            OutputStream os = exchange.getResponseBody();
            os.write(data.getBytes());
            os.close();
        } catch (IOException e) {
            LOGGER.warning("" + e);
        }
        if (code >= 400) {
            LOGGER.warning("HTTP response with code " + code + ": " + data);
        }
    }
}