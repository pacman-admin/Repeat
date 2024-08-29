package core.keyChain;

import java.awt.event.KeyEvent;

/**
 * The state of lock keys on the keyboard (numslock, capslock, scrolllock).
 * Instances of this class is immutable.
 */
@SuppressWarnings("unused")
public final class KeyboardState {
    private boolean numslockLocked;
    private boolean capslockLocked;
    private boolean scrollLockLocked;
    private boolean shiftLocked;

    private KeyboardState(boolean numlockLocked, boolean capslockLocked, boolean scrollLockLocked, boolean isShiftLocked) {
        this.numslockLocked = numlockLocked;
        this.capslockLocked = capslockLocked;
        this.scrollLockLocked = scrollLockLocked;
        this.shiftLocked = isShiftLocked;
    }

    public static KeyboardState getDefault() {
        return new KeyboardState(false, false, false, false);
    }

    private static KeyboardState of(boolean numlockLocked, boolean capslockLocked, boolean scrollLockLocked, boolean isShiftLocked) {
        return new KeyboardState(numlockLocked, capslockLocked, scrollLockLocked, isShiftLocked);
    }

    public KeyboardState changeWith(KeyStroke stroke) {
        int key = stroke.getKey();
        boolean pressed = stroke.isPressed();

        if (key == KeyEvent.VK_SHIFT) {
            return withShiftLocked(pressed);
        } else if (key == KeyEvent.VK_NUM_LOCK) {
            return withNumslock(pressed);
        } else if (key == KeyEvent.VK_CAPS_LOCK) {
            return withCapslock(pressed);
        } else if (key == KeyEvent.VK_SCROLL_LOCK) {
            return withScrollLock(pressed);
        }

        return this;
    }

    @SuppressWarnings("unused")
    public KeyboardState withNumslockOn() {
        return clone().setNumslockLocked(true);
    }

    public KeyboardState withNumslockOff() {
        return clone().setNumslockLocked(false);
    }

    private KeyboardState withNumslock(boolean state) {
        return clone().setNumslockLocked(state);
    }

    public KeyboardState withCapslockOn() {
        return clone().setCapslockLocked(true);
    }

    public KeyboardState withCapslockOff() {
        return clone().setCapslockLocked(false);
    }

    private KeyboardState withCapslock(boolean state) {
        return clone().setCapslockLocked(state);
    }

    public KeyboardState withScrollLockOn() {
        return clone().setScrollLockLocked(true);
    }

    public KeyboardState withScrollLockOff() {
        return clone().setScrollLockLocked(false);
    }

    private KeyboardState withScrollLock(boolean state) {
        return clone().setScrollLockLocked(state);
    }

    public KeyboardState withShiftLocked() {
        return clone().setShiftLocked(true);
    }

    public KeyboardState withShiftUnlocked() {
        return clone().setShiftLocked(false);
    }

    private KeyboardState withShiftLocked(boolean state) {
        return clone().setShiftLocked(state);
    }

    @Override
    public KeyboardState clone() {
        return of(numslockLocked, capslockLocked, scrollLockLocked, shiftLocked);
    }

    public boolean isNumslockLocked() {
        return numslockLocked;
    }

    private KeyboardState setNumslockLocked(boolean numslockLocked) {
        this.numslockLocked = numslockLocked;
        return this;
    }

    public boolean isCapslockLocked() {
        return capslockLocked;
    }

    private KeyboardState setCapslockLocked(boolean capslockLocked) {
        this.capslockLocked = capslockLocked;
        return this;
    }

    public boolean isScrollLockLocked() {
        return scrollLockLocked;
    }

    private KeyboardState setScrollLockLocked(boolean scrollLockLocked) {
        this.scrollLockLocked = scrollLockLocked;
        return this;
    }

    public boolean isShiftLocked() {
        return shiftLocked;
    }

    private KeyboardState setShiftLocked(boolean shiftLocked) {
        this.shiftLocked = shiftLocked;
        return this;
    }
}
