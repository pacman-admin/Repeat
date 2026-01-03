package core.userDefinedTask;

@Deprecated
public class Tools {
    @Deprecated
    private Tools() {
        //This class is uninstantiable
    }

    @Deprecated
    public static String getClipboard() {
        return Clipboard.get();
    }

    @Deprecated
    public static void setClipboard(String data) {
        Clipboard.set(data);
    }
}