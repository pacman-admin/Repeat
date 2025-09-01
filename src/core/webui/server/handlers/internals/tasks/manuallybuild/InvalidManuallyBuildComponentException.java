package core.webui.server.handlers.internals.tasks.manuallybuild;

@SuppressWarnings("MissingSerialAnnotation")
class InvalidManuallyBuildComponentException extends Exception {
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