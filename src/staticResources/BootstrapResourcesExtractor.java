package staticResources;

import java.io.IOException;
import java.net.URISyntaxException;

interface BootstrapResourcesExtractor {
    void extractResources() throws IOException, URISyntaxException;
}
