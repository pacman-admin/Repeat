/**
 * Copyright 2025 Langdon Staab and HP Truong
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
 * @author HP Truong
 */
package core.webui.webcommon;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import staticResources.BootStrapResources;
import staticResources.WebUIResources;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class StaticFileServingHandler implements HttpHandler {
    private static final Logger LOGGER = Logger.getLogger(StaticFileServingHandler.class.getName());
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        LOGGER.fine("Path is " + exchange.getRequestURI());
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            HttpServerUtilities.prepareTextResponse(exchange, 400, "I only accept GET requests.");
        }
        String path = "";
        try {
            URI uri = exchange.getRequestURI();
            path = new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), null, // Ignore the query part of the input url.
                    uri.getFragment()).toString();
        } catch (URISyntaxException e) {
            LOGGER.log(Level.WARNING, "Encountered exception when trying to remove query parameters.", e);
            HttpServerUtilities.prepareTextResponse(exchange, 500, "Encountered exception when trying to remove query parameters.");
        }

        try {
            InputStream inputStream = BootStrapResources.getStaticContentStream(WebUIResources.STATIC_RESOURCES_PREFIX + path.substring("/static/".length()));
            LOGGER.fine("Accessing " + path + "...");
            if (inputStream == null) {
                LOGGER.warning("Content could not be accessed:\n" + path);
                HttpServerUtilities.prepareTextResponse(exchange, 404, String.format("File does not exist %s.", path));
            }
            exchange.sendResponseHeaders(200, 0);
            assert inputStream != null;
            long length = inputStream.transferTo(exchange.getResponseBody());
            inputStream.close();
            exchange.getResponseBody().close();
            LOGGER.fine("Served " + path + ". Bytes: " + length);
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.WARNING, "Content could not be accessed:\n" + path, e);
            HttpServerUtilities.prepareTextResponse(exchange, 404, "Could not access file." + path);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Content could not be accessed:\n" + path, e);
            HttpServerUtilities.prepareTextResponse(exchange, 500, "Could not access file." + path);
        }
    }
}