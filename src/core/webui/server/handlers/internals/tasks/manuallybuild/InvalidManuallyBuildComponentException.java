package core.webui.server.handlers.internals.tasks.manuallybuild;

import java.io.Serial;

public final class InvalidManuallyBuildComponentException extends Exception {
    @Serial
    private static final long serialVersionUID = 5763742112199815619L;

    private final String message;

    public InvalidManuallyBuildComponentException(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}