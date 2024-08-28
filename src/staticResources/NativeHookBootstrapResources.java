package staticResources;

import org.simplenativehooks.staticResources.BootStrapResources;

import java.io.IOException;
import java.net.URISyntaxException;

public final class NativeHookBootstrapResources implements BootstrapResourcesExtractor {

    @Override
    public void extractResources() throws IOException, URISyntaxException {
        BootStrapResources.extractResources();
    }
}
