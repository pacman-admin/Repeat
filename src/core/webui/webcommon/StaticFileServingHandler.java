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
package core.webui.webcommon;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.nio.protocol.BasicAsyncResponseProducer;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;
import staticResources.BootStrapResources;
import staticResources.WebUIResources;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StaticFileServingHandler extends HttpSimpleAsyncRequestHandler {

    private static final Logger LOGGER = Logger.getLogger(StaticFileServingHandler.class.getName());

    public StaticFileServingHandler() {
    }

    @Override
    public Void handleRequest(HttpRequest request, HttpAsyncExchange exchange, HttpContext context) throws IOException {
        LOGGER.fine("Path is " + request.getRequestLine().getUri());
        if (!request.getRequestLine().getMethod().equalsIgnoreCase("GET")) {
            return HttpServerUtilities.prepareTextResponse(exchange, 400, "Only accept GET requests.");
        }

        String requestUri = request.getRequestLine().getUri();
        if (!requestUri.startsWith("/static/")) {
            return HttpServerUtilities.prepareTextResponse(exchange, 500, "URI must start with '/static/'.");
        }

        String uriWithoutParamter = "";
        try {
            URI uri = new URI(requestUri);
            uriWithoutParamter = new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), null, // Ignore the query part of the input url.
                    uri.getFragment()).toString();
        } catch (URISyntaxException e) {
            LOGGER.log(Level.WARNING, "Encountered exception when trying to remove query parameters.", e);
            return HttpServerUtilities.prepareTextResponse(exchange, 500, "Encountered exception when trying to remove query parameters.");
        }

        String path = uriWithoutParamter.substring("/static/".length());
        String decodedPath = URLDecoder.decode(path, "UTF-8");
        if (decodedPath.contains("./") || decodedPath.contains("..") || decodedPath.endsWith("/")) {
            return HttpServerUtilities.prepareTextResponse(exchange, 404, String.format("File does not exist %s.", path));
        }

        HttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.SC_OK);
        response.addHeader("Cache-Control", "max-age=3600"); // Max age = 1 hour.
        String contentType = contentType(decodedPath);
        InputStream inputStream = BootStrapResources.getStaticContentStream(WebUIResources.STATIC_RESOURCES_PREFIX + decodedPath);
        if (inputStream == null) LOGGER.warning("Content could not be accessed!!!:\n" + path + ", " + decodedPath);
        LOGGER.fine("Accessing " + path + ", " + decodedPath + "...");
        InputStreamEntity body = new InputStreamEntity(inputStream, ContentType.create(contentType));
        response.setEntity(body);
        exchange.submitResponse(new BasicAsyncResponseProducer(response));
        return null;
    }

    private String contentType(String filePath) {
        if (filePath.endsWith(".js")) {
            return "application/javascript";
        }
        if (filePath.endsWith(".css")) {
            return "text/css";
        }
        if (filePath.endsWith(".htm") || filePath.endsWith(".html")) {
            return "text/html";
        }
        if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg") || filePath.endsWith(".jpe")) {
            return "image/jpeg";
        }
        if (filePath.endsWith(".png")) {
            return "image/png";
        }
        if (filePath.endsWith(".gif")) {
            return "image/gif";
        }
        return "text/plain";
    }
}