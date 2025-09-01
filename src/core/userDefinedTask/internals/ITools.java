package core.userDefinedTask.internals;

import java.io.File;

public interface ITools {
	String getClipboard();
	boolean setClipboard(String data);
	String execute(String command);
	String execute(String command, String cwd);
	String execute(String command, File cwd);
}
