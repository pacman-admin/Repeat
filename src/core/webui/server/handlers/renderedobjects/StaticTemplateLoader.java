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
package core.webui.server.handlers.renderedobjects;

import freemarker.cache.TemplateLoader;
import staticResources.BootStrapResources;
import staticResources.WebUIResources;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class StaticTemplateLoader implements TemplateLoader {

    private static final StaticTemplateLoader INSTANCE = new StaticTemplateLoader();

    private StaticTemplateLoader() {
    }

    public static StaticTemplateLoader of() {
        return INSTANCE;
    }

    @Override
    public void closeTemplateSource(Object arg0) {

    }

    @Override
    public Object findTemplateSource(String path) {
        // Whatever returned here will be used to pass into other methods.
        // According to documentation at
        // https://freemarker.apache.org/docs/api/freemarker/cache/TemplateLoader.html#findTemplateSource-java.lang.String,
        // this object must implement hashCode and equals.
        return path;
    }

    @Override
    public long getLastModified(Object path) {
        return 0; // Never modified.
    }

    @Override
    public Reader getReader(Object path, String locale) {
        InputStream content = BootStrapResources.getStaticContentStream(WebUIResources.TEMPLATES_RESOURCES_PREFIX + path);
        if (content == null) throw new RuntimeException("Content could not be accessed!!!:\n" + path);
        System.out.println("Accessing "+path+"...");
        return new InputStreamReader(content);
    }

}
