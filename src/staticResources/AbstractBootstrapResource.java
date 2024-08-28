package staticResources;

import utilities.FileUtility;
import utilities.Function;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Logger;

public abstract class AbstractBootstrapResource implements BootstrapResourcesExtractor {

    private static final Logger LOGGER = Logger.getLogger(AbstractBootstrapResource.class.getName());

    @Override
    public void extractResources() throws IOException, URISyntaxException {
        if (!FileUtility.createDirectory(getExtractingDest().getAbsolutePath())) {
            LOGGER.warning("Failed to extract " + getName() + " resources");
            return;
        }

        final String path = getRelativeSourcePath();
        FileUtility.extractFromCurrentJar(path, getExtractingDest(), new Function<>() {
            @Override
            public Boolean apply(String name) {
                return correctExtension(name);
            }
        }, new Function<>() {
            @Override
            public Boolean apply(String name) {
                return postProcessing();
            }
        });
    }

    private boolean postProcessing() {
        return true;
    }

    protected abstract boolean correctExtension(String name);

    protected abstract String getRelativeSourcePath();

    protected abstract File getExtractingDest();

    protected abstract String getName();
}
