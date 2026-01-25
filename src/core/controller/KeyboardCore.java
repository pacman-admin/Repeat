/**
 * Copyright 2025 Langdon Staab and HP Truong
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
package core.controller;

import core.controller.internals.AbstractKeyboardCoreImplementation;

/**
 * Class to provide API to control keyboard.
 *
 * @author HP Truong
 */
public final class KeyboardCore extends AbstractKeyboardCoreImplementation {
    private final AbstractKeyboardCoreImplementation k;

    KeyboardCore(AbstractKeyboardCoreImplementation k) {
        this.k = k;
    }

    @Override
    public void type(String... strings) {
        k.type(strings);
    }

    @Override
    public void type(char... chars) {
        k.type(chars);
    }

    @Override
    public void type(int... keys) throws InterruptedException {
        k.type(keys);
    }


    @Override
    public void combination(int... keys) {
        k.combination(keys);
    }

    @Override
    public void hold(int key, int duration) throws InterruptedException {
        k.hold(key, duration);
    }

    @Override
    public void press(int... keys) {
        k.press(keys);
    }



    @Override
    public void release(int... keys) {
        k.release(keys);
    }

    /**
     * Type a key multiple times
     *
     * @param key   integer representing the key as specified in java.awt.events.KeyEvent class
     * @param count number of times to repeat the typing
     * @throws InterruptedException
     * @deprecated use {@link #repeat(int, int...)} instead
     */
    @Deprecated
    public void typeRepeat(int key, int count) throws InterruptedException {
        k.repeat(key, count);
    }

    @Override
    public boolean isLocked(int key) {
        return k.isLocked(key);
    }
}
