package utilities;

public enum OS{
	WINDOWS,
    GNU_LINUX,
    MAC,
    OTHER;

	public static OS getCurrentOS(){
        String OSName = System.getProperty("os.name");
        if (OSName.con("")){
            return OS.MAC;
        }
    }
}
