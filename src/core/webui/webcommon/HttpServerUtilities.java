package core.webui.webcommon;

import argo.jdom.JsonField;
import argo.jdom.JsonNode;
import org.apache.http.*;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.nio.protocol.BasicAsyncResponseProducer;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import utilities.json.JSONUtility;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HttpServerUtilities {

    private static final Logger LOGGER = Logger.getLogger(HttpServerUtilities.class.getName());

    private HttpServerUtilities() {
    }

    public static Map<String, String> parseGetParameters(String url) {
        try {
            List<NameValuePair> paramList = URLEncodedUtils.parse(new URI(url), StandardCharsets.UTF_8);
            Map<String, String> params = new HashMap<>();
            for (NameValuePair param : paramList) {
                params.put(param.getName(), param.getValue());
                LOGGER.fine(param.getName() + ", " + param.getValue());
            }
            return params;
        } catch (URISyntaxException e) {
            LOGGER.log(Level.WARNING, "Exception when parsing URL.", e);
            return null;
        }
    }

    public static JsonNode parsePostParameters(HttpRequest request) {
        byte[] content = getPostContent(request);
        if (content == null) {
            LOGGER.warning("Failed to get POST content.");
            return null;
        }

        return getPostParameters(content);
    }

    public static Map<String, String> parseSimplePostParameters(HttpRequest request) {
        byte[] content = getPostContent(request);
        if (content == null) {
            LOGGER.warning("Failed to get POST content.");
            return null;
        }

        return getSimplePostParameters(content);
    }

    public static byte[] getPostContent(HttpRequest request) {
        if (!(request instanceof HttpEntityEnclosingRequest)) {
            LOGGER.warning("Unknown request type for POST request " + request.getClass());
            return null;
        }
        HttpEntityEnclosingRequest entityRequest = (HttpEntityEnclosingRequest) request;
        HttpEntity entity = entityRequest.getEntity();
        if (!(entity instanceof BasicHttpEntity)) {
            LOGGER.warning("Unknown entity type for POST request " + entity.getClass());
            return null;
        }
        BasicHttpEntity basicEntity = (BasicHttpEntity) entity;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            basicEntity.writeTo(buffer);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to read all request content.", e);
            return null;
        }
        return buffer.toByteArray();
    }

    private static JsonNode getPostParameters(byte[] content) {
        String postContent = new String(content, StandardCharsets.UTF_8);
        JsonNode node = JSONUtility.jsonFromString(postContent);
        if (node == null) {
            LOGGER.warning("Failed to parse content into JSON.");
            return null;
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
            String value = null;
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


    public static Void prepareResponse(HttpAsyncExchange exchange, int code, byte[] data) throws IOException {
        HttpResponse response = exchange.getResponse();
        response.setStatusCode(code);
        response.setEntity(new ByteArrayEntity(data));
        exchange.submitResponse(new BasicAsyncResponseProducer(response));
        return null;
    }

    public static Void prepareHttpResponse(HttpAsyncExchange exchange, int code, String data) throws IOException {
        return prepareStringResponse(exchange, code, data, "text/html");
    }

    public static Void prepareTextResponse(HttpAsyncExchange exchange, int code, String data) throws IOException {
        return prepareStringResponse(exchange, code, data, "text/plain; charset=utf-8");
    }

    public static Void prepareJsonResponse(HttpAsyncExchange exchange, int code, JsonNode data) throws IOException {
        return prepareStringResponse(exchange, code, JSONUtility.jsonToSingleLineString(data), "application/json; charset=utf-8");
    }

    private static Void prepareStringResponse(HttpAsyncExchange exchange, int code, String data, String contentType) throws IOException {
        HttpResponse response = exchange.getResponse();
        response.setStatusCode(code);
        StringEntity entity = new StringEntity(data, "UTF-8");
        entity.setContentEncoding("UTF-8");
        entity.setContentType(contentType);
        response.setEntity(entity);
        exchange.submitResponse(new BasicAsyncResponseProducer(response));

        if (code != 200) {
            LOGGER.warning("HTTP response with code " + code + ": " + data);
        }
        return null;
    }
}
