/**
 * Copyright 2025 Langdon Staab
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Langdon Staab
 * @author HP Truong
 */
package utilities.natives.processes;

import com.sun.jna.Native;
import com.sun.jna.platform.unix.X11;
import com.sun.jna.platform.unix.X11.Display;
import com.sun.jna.platform.unix.X11.WindowByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import utilities.Function;
import utilities.OS;
import utilities.OSIdentifier;

import java.util.logging.Logger;

/**
 * Provides interaction with Linux processes via X11 interface.
 * Reference C code: <a href="https://gist.github.com/kui/2622504">...</a>
 */
public class X11NativeProcessUtil {

    private static final Logger LOGGER = Logger.getLogger(X11NativeProcessUtil.class.getName());

    public static NativeProcessUtil.NativeWindowInfo getActiveWindowInfo() {
        final X11 x11 = X11.INSTANCE;
        final X11Extended xlib = X11Extended.INSTANCE;

        Display display = xlib.XOpenDisplay(null);
        if (display == null) {
            LOGGER.info("No X11 display found.");
            return NativeProcessUtil.NativeWindowInfo.of("", "");
        }
        WindowByReference currentWindowReference = new WindowByReference();
        IntByReference revertToReturn = new IntByReference();
        xlib.XGetInputFocus(display, currentWindowReference, revertToReturn);
        X11.Window window = currentWindowReference.getValue();
        if (window == null) {
            LOGGER.info("No input focus window found. " + revertToReturn.getValue());
            return NativeProcessUtil.NativeWindowInfo.of("", "");
        }

        long topWindow = findTopWindow(x11, display, currentWindowReference);
        X11.XTextProperty name = new X11.XTextProperty();
        x11.XGetWMName(display, new X11.Window(topWindow), name);

        return NativeProcessUtil.NativeWindowInfo.of(name.value == null ? "" : name.value, "");
    }

    private static long findTopWindow(X11 x11, Display display, X11.WindowByReference current) {
        X11.Window window = current.getValue();
        X11.WindowByReference rootRef = new X11.WindowByReference();
        PointerByReference childrenRef = new PointerByReference();
        IntByReference childCountRef = new IntByReference();

        while (current.getValue().longValue() != rootRef.getValue().longValue()) {
            window = new X11.Window(current.getValue().longValue());

            int res = x11.XQueryTree(display, window, rootRef, current, childrenRef, childCountRef);
            if (res != 0) {
                x11.XFree(childrenRef.getValue());
            }
        }
        return window.longValue();
    }

    /**
     * Prints all windows on display.
     */
    @SuppressWarnings("unused")
    private static void printAllUnderRoot(X11 x11, Display display) {
        X11.Window root = x11.XDefaultRootWindow(display);
        recurse(x11, display, root, 0);
    }

    /**
     * Prints all windows under a root.
     */
    private static void recurse(X11 x11, Display display, X11.Window root, int depth) {
        X11.WindowByReference windowRef = new X11.WindowByReference();
        X11.WindowByReference parentRef = new X11.WindowByReference();
        PointerByReference childrenRef = new PointerByReference();
        IntByReference childCountRef = new IntByReference();

        x11.XQueryTree(display, root, windowRef, parentRef, childrenRef, childCountRef);
        if (childrenRef.getValue() == null) {
            return;
        }

        long[] ids;

        if (Native.LONG_SIZE == Long.BYTES) {
            ids = childrenRef.getValue().getLongArray(0, childCountRef.getValue());
        } else if (Native.LONG_SIZE == Integer.BYTES) {
            int[] intIds = childrenRef.getValue().getIntArray(0, childCountRef.getValue());
            ids = new long[intIds.length];
            for (int i = 0; i < intIds.length; i++) {
                ids[i] = intIds[i];
            }
        } else {
            throw new IllegalStateException("Unexpected size for Native.LONG_SIZE" + Native.LONG_SIZE);
        }

        for (long id : ids) {
            if (id == 0) {
                continue;
            }
            X11.Window window = new X11.Window(id);
            X11.XTextProperty name = new X11.XTextProperty();
            x11.XGetWMName(display, window, name);

            //System.out.println("Depth=" + depth + " (" + id + "):" + String.join("", Collections.nCopies(depth, "  ")) + name.value);
            x11.XFree(name.getPointer());

            recurse(x11, display, window, depth + 1);
        }
    }

    public interface X11Extended extends X11 {
        X11Extended INSTANCE = new Function<Void, X11Extended>() {
            @Override
            public X11Extended apply(Void d) {
                if (OSIdentifier.getCurrentOS() != OS.LINUX) {
                    return null;
                }
                return Native.load("X11", X11Extended.class);
            }
        }.apply(null);

        void XGetInputFocus(Display display, WindowByReference focusReturn, IntByReference revertToReturn);
    }
}