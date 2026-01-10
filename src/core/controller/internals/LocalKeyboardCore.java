package core.controller.internals;

import core.config.Config;
import core.userDefinedTask.Clipboard;
import core.userDefinedTask.Tools;
import utilities.Function;
import utilities.OSIdentifier;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class LocalKeyboardCore extends AbstractKeyboardCoreImplementation {

    private static final Logger LOGGER = Logger.getLogger(LocalKeyboardCore.class.getName());
    private static final Set<Integer> OSX_FLAG_KEYS;
    private static final HashMap<Character, Function<LocalKeyboardCore, Void>> charShiftType;
    private static final Toolkit toolkit = Toolkit.getDefaultToolkit();
    private static final int TYPE_DURATION_MS = 20;
    // In OSX we somehow need to have a delay between pressing combinations.
    private static final int OSX_KEY_FLAG_DELAY_MS = 70;

    static {
        OSX_FLAG_KEYS = new HashSet<>();
        OSX_FLAG_KEYS.add(KeyEvent.VK_CONTROL);
        OSX_FLAG_KEYS.add(KeyEvent.VK_ALT);
        OSX_FLAG_KEYS.add(KeyEvent.VK_META);
        OSX_FLAG_KEYS.add(KeyEvent.VK_SHIFT);

        charShiftType = new HashMap<>();

        final int[] keys = new int[]{KeyEvent.VK_1, KeyEvent.VK_2, KeyEvent.VK_3, KeyEvent.VK_4, KeyEvent.VK_5, KeyEvent.VK_6, KeyEvent.VK_7, KeyEvent.VK_8, KeyEvent.VK_9, KeyEvent.VK_0, KeyEvent.VK_MINUS, KeyEvent.VK_EQUALS, KeyEvent.VK_COMMA, KeyEvent.VK_PERIOD, KeyEvent.VK_SLASH, KeyEvent.VK_BACK_SLASH, KeyEvent.VK_OPEN_BRACKET, KeyEvent.VK_CLOSE_BRACKET, KeyEvent.VK_SEMICOLON, KeyEvent.VK_QUOTE, KeyEvent.VK_BACK_QUOTE};

        Character[] inputs = new Character[]{'!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '_', '+', '<', '>', '?', '|', '{', '}', ':', '"', '~'};

        for (int i = 0; i < inputs.length; i++) {
            final int index = i;
            charShiftType.put(inputs[index], new Function<>() {
                @Override
                public Void apply(LocalKeyboardCore c) {
                    c.press(KeyEvent.VK_SHIFT);
                    c.press(keys[index]);
                    c.release(KeyEvent.VK_SHIFT);
                    c.release(keys[index]);
                    return null;
                }
            });
        }
    }

    private final Config config;
    private final Robot controller;

    /**
     * Only use this constructor within the {@link core.controller.Core} class.
     */
    public LocalKeyboardCore(Config config, Robot controller) {
        this.config = config;
        this.controller = controller;
    }

    private void pasteString(String s) {
        Tools.setClipboard(s);
        switch (OSIdentifier.getCurrentOS()) {
            case MAC:
                press(KeyEvent.VK_META, KeyEvent.VK_V);
                controller.delay(100);
                release(KeyEvent.VK_V, KeyEvent.VK_META);
                return;
            case WINDOWS:
                press(KeyEvent.VK_SHIFT, KeyEvent.VK_INSERT);
                controller.delay(100);
                release(KeyEvent.VK_INSERT, KeyEvent.VK_SHIFT);
                return;
            default:
                LOGGER.warning("Using the clipboard to type strings works on macOS and Windows only.");
                config.setUseClipboardToTypeString(false);
                type(s);
        }
    }

    private void typeSingleString(String string) {
        if (config.isUseClipboardToTypeString()) {
            String existing = Clipboard.get();
            pasteString(string);
            if (!existing.isBlank()) {
                Clipboard.set(existing);
            }
            return;
        }
        type(string.toCharArray());
    }

    @Override
    public void type(String... strings) {
        for (String s : strings) {
            typeSingleString(s);
        }
    }

    private void typeSingleCharacter(char c) {
        if (Character.isAlphabetic(c)) {
            typeAlphabetic(c);
        } else if (charShiftType.containsKey(c)) {
            charShiftType.get(c).apply(this);
        } else if (!typeSpecialChar(c)) {
            typeUnknown(c);
        }
    }

    @Override
    public void type(char... chars) {
        for (char c : chars) {
            typeSingleCharacter(c);
        }
    }

    private boolean typeSpecialChar(char c) {
        switch (c) {
            case '\t':
                press(KeyEvent.VK_TAB);
                release(KeyEvent.VK_TAB);
                return true;
            case '\n':
                press(KeyEvent.VK_ENTER);
                release(KeyEvent.VK_ENTER);
                return true;
            default:
                return false;
        }
    }

    private void typeAlphabetic(char c) {
        if (Character.isUpperCase(c)) {
            press(KeyEvent.VK_SHIFT);
        }
        controller.keyPress(Character.toUpperCase(c));
        controller.keyRelease(Character.toUpperCase(c));

        if (Character.isUpperCase(c)) {
            release(KeyEvent.VK_SHIFT);
        }
    }


    private void typeUnknown(char c) {
        int converted = KeyEvent.getExtendedKeyCodeForChar(c);
        controller.keyPress(converted);
        controller.keyRelease(converted);
    }

    @Override
    public void type(int... keys) throws InterruptedException {
        for (int key : keys) {
            hold(key, TYPE_DURATION_MS);
        }
    }

    @Override
    public void combination(int... keys) {
        press(keys);

        for (int i = keys.length - 1; i >= 0; i--) {
            release(keys[i]);
        }
    }

    @Override
    public void hold(int key, int duration) throws InterruptedException {
        press(key);

        if (duration >= 0) {
            Thread.sleep(duration);
        }

        release(key);
    }

    private void pressSingleKey(int key) {
        controller.keyPress(key);
        if (OSIdentifier.isMac() && OSX_FLAG_KEYS.contains(key)) {
            controller.delay(OSX_KEY_FLAG_DELAY_MS);
        }
    }

    @Override
    public void press(int... keys) {
        for (int key : keys) {
            pressSingleKey(key);
        }
    }

    private void releaseSingleKey(int key) {
        controller.keyRelease(key);
        if (OSIdentifier.isMac() && OSX_FLAG_KEYS.contains(key)) {
            controller.delay(OSX_KEY_FLAG_DELAY_MS);
        }
    }

    @Override
    public void release(int... keys) {
        for (int key : keys) {
            releaseSingleKey(key);
        }
    }

    @Override
    public boolean isLocked(int key) {
        return toolkit.getLockingKeyState(key);
    }
}
