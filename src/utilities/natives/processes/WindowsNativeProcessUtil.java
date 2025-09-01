package utilities.natives.processes;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.ptr.PointerByReference;
import utilities.OSIdentifier;

/**
 * Provides interaction with Window processes via Windows native DLLs.
 */
final class WindowsNativeProcessUtil {

    private static final int MAX_TITLE_LENGTH = 1024;


    private WindowsNativeProcessUtil() {
    }

    public static NativeProcessUtil.NativeWindowInfo getActiveWindowInfo() {
        String title = getActiveWindowTitle();
        String processName = getActiveWindowProcessName();

        return NativeProcessUtil.NativeWindowInfo.of(title, processName);
    }

    private static String getActiveWindowTitle() {
        char[] buffer = new char[MAX_TITLE_LENGTH * 2];
        HWND window = User32DLL.GetForegroundWindow();
        User32DLL.GetWindowTextW(window, buffer, MAX_TITLE_LENGTH);
        return Native.toString(buffer);
    }

    private static String getActiveWindowProcessName() {
        char[] buffer = new char[MAX_TITLE_LENGTH * 2];
        PointerByReference pointer = new PointerByReference();
        HWND window = User32DLL.GetForegroundWindow();
        User32DLL.GetWindowThreadProcessId(window, pointer);
        Pointer process = Kernel32.OpenProcess(Kernel32.PROCESS_QUERY_INFORMATION | Kernel32.PROCESS_VM_READ, false, pointer.getValue());
        Psapi.GetModuleBaseNameW(process, null, buffer, MAX_TITLE_LENGTH);
        return Native.toString(buffer);
    }

    static class Psapi {
        static {
            if (OSIdentifier.isWindows()) {
                Native.register("psapi");
            }
        }

        // See https://learn.microsoft.com/en-us/windows/win32/intl/conventions-for-function-prototypes.
        // Suffix "W" indicates Unicode string instead of "A" indicating ANSI string.
        public static native int GetModuleBaseNameW(Pointer hProcess, Pointer hmodule, char[] lpBaseName, int size);
    }

    static class Kernel32 {
        // https://learn.microsoft.com/en-us/windows/win32/procthread/process-security-and-access-rights
        static int PROCESS_QUERY_INFORMATION = 0x0400;
        static int PROCESS_VM_READ = 0x0010;

        static {
            if (OSIdentifier.isWindows()) {
                Native.register("kernel32");
            }
        }

        // https://learn.microsoft.com/en-us/windows/win32/api/processthreadsapi/nf-processthreadsapi-openprocess
        public static native Pointer OpenProcess(int dwDesiredAccess, boolean bInheritHandle, Pointer pointer);
    }

    static class User32DLL {
        static {
            if (OSIdentifier.isWindows()) {
                Native.register("user32");
            }
        }

        // https://learn.microsoft.com/en-us/windows/win32/api/winuser/nf-winuser-getwindowthreadprocessid
        public static native int GetWindowThreadProcessId(HWND hWnd, PointerByReference pref);

        // https://learn.microsoft.com/en-us/windows/win32/api/winuser/nf-winuser-getforegroundwindow
        public static native HWND GetForegroundWindow();

        // https://learn.microsoft.com/en-us/windows/win32/api/winuser/nf-winuser-getwindowtextw
        public static native int GetWindowTextW(HWND hWnd, char[] lpString, int nMaxCount);
    }
}
