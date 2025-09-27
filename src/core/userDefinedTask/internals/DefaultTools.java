package core.userDefinedTask.internals;

public class DefaultTools implements ITools {

    private static final DefaultTools INSTANCE = new DefaultTools();
    private ITools executor = LocalTools.of();

    public static synchronized void setExecutor(ITools executor) {
        INSTANCE.executor = executor;
    }

    public static DefaultTools get() {
        return INSTANCE;
    }

    @Override
    public String getClipboard() {
        return executor.getClipboard();
    }

    @Override
    public boolean setClipboard(String data) {
        return executor.setClipboard(data);
    }

}
