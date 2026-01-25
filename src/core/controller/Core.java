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

import core.config.Config;
import core.controller.internals.LocalKeyboardCore;
import core.controller.internals.LocalMouseCore;

import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Core {

    private static final Logger LOGGER = Logger.getLogger(Core.class.getName());

    private final MouseCore mouse;
    private final KeyboardCore keyboard;

    private Core(MouseCore mouse, KeyboardCore keyboard) {
        this.mouse = mouse;
        this.keyboard = keyboard;
    }

    public static Core local(Config config) {
        Robot controller = null;
        try {
            controller = new Robot();
        } catch (AWTException e) {
            LOGGER.log(Level.SEVERE, "Exception constructing controller", e);
            System.exit(1);
        }

        return new Core(new MouseCore(new LocalMouseCore(controller)), new KeyboardCore(new LocalKeyboardCore(config, controller)));
    }

    /**
     * Blocking wait the current action for an amount of time
     *
     * @param duration wait duration in milliseconds
     * @throws InterruptedException
     */
    private void blockingWait(int duration) throws InterruptedException {
        Thread.sleep(duration);
    }

    /**
     * A short alias for blockingWait
     *
     * @param duration wait duration in milliseconds
     * @throws InterruptedException
     */
    public void delay(int duration) throws InterruptedException {
        blockingWait(duration);
    }

    /**
     * Getter for the mouse attribute. See {@link core.controller.MouseCore} class
     *
     * @return The mouse controller attribute
     */
    public MouseCore mouse() {
        return mouse;
    }

    /**
     * Getter for the mouse attribute. See {@link core.controller.KeyboardCore} class
     *
     * @return The keyboard controller attribute
     */
    public KeyboardCore keyBoard() {
        return keyboard;
    }
}
