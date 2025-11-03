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
package core.recorder;

import core.controller.Core;
import core.controller.CoreProvider;
import core.languageHandler.Language;
import core.languageHandler.sourceGenerator.AbstractSourceGenerator;
import core.languageHandler.sourceGenerator.AbstractSourceGenerator.Device;
import core.languageHandler.sourceGenerator.JavaSourceGenerator;
import core.languageHandler.sourceGenerator.ManuallyBuildSourceGenerator;
import core.scheduler.SchedulingData;
import globalListener.GlobalListenerFactory;
import org.simplenativehooks.events.NativeKeyEvent;
import org.simplenativehooks.events.NativeMouseEvent;
import org.simplenativehooks.listeners.AbstractGlobalKeyListener;
import org.simplenativehooks.listeners.AbstractGlobalMouseListener;
import org.simplenativehooks.utilities.Function;

import java.util.HashMap;

public class Recorder {

    public static final int MODE_NORMAL = 0;
    public static final int MODE_MOUSE_CLICK_ONLY = 1;

    private static final float NO_SPEEDUP = 1f;
    private float speedup;

    private long startTime;
    private int mode;

    private final TaskScheduler taskScheduler;

    private final AbstractGlobalKeyListener keyListener;
    private final AbstractGlobalMouseListener mouseListener;

    private final HashMap<Language, AbstractSourceGenerator> sourceGenerators;

    public Recorder(CoreProvider coreProvider) {
        final Core controller = coreProvider.getLocal();
        taskScheduler = new TaskScheduler();

        speedup = NO_SPEEDUP;

        sourceGenerators = new HashMap<>();
        sourceGenerators.put(Language.JAVA, new JavaSourceGenerator());
        //sourceGenerators.put(Language.PYTHON, new PythonSourceGenerator());
        //sourceGenerators.put(Language.CSHARP, new CSharpSourceGenerator());
        sourceGenerators.put(Language.MANUAL_BUILD, new ManuallyBuildSourceGenerator());

        /*************************************************************************************************/
        keyListener = GlobalListenerFactory.of().createGlobalKeyListener();
        keyListener.setKeyPressed(new Function<>() {
            @Override
            public Boolean apply(final NativeKeyEvent r) {
                final int code = r.getKey();
                final long time = System.currentTimeMillis() - startTime;
                taskScheduler.addTask(new SchedulingData<>(time, () -> controller.keyBoard().press(code)));

                for (AbstractSourceGenerator generator : sourceGenerators.values()) {
                    generator.submitTask(time, Device.KEYBOARD, "press", new int[]{code});
                }
                return true;
            }
        });

        keyListener.setKeyReleased(new Function<>() {
            @Override
            public Boolean apply(final NativeKeyEvent r) {
                final int code = r.getKey();
                final long time = System.currentTimeMillis() - startTime;
                taskScheduler.addTask(new SchedulingData<>(time, () -> controller.keyBoard().release(code)));

                for (AbstractSourceGenerator generator : sourceGenerators.values()) {
                    generator.submitTask(time, Device.KEYBOARD, "release", new int[]{code});
                }
                return true;
            }
        });

        /*************************************************************************************************/
        mouseListener = GlobalListenerFactory.of().createGlobalMouseListener();
        mouseListener.setMouseReleased(new Function<>() {
            @Override
            public Boolean apply(final NativeMouseEvent r) {
                final int code = r.getButton();
                final long time = System.currentTimeMillis() - startTime;
                taskScheduler.addTask(new SchedulingData<>(time, () -> {
                    if (mode == MODE_MOUSE_CLICK_ONLY) {
                        controller.mouse().move(r.getX(), r.getY());
                    }
                    controller.mouse().release(code);
                }));


                for (AbstractSourceGenerator generator : sourceGenerators.values()) {
                    if (mode == MODE_MOUSE_CLICK_ONLY) {
                        generator.submitTask(time, Device.MOUSE, "move", new int[]{r.getX(), r.getY()});
                        generator.submitTask(time + 5, Device.MOUSE, "release", new int[]{code});
                    } else {
                        generator.submitTask(time, Device.MOUSE, "release", new int[]{code});
                    }
                }
                return true;
            }
        });

        mouseListener.setMousePressed(new Function<>() {
            @Override
            public Boolean apply(final NativeMouseEvent r) {
                final int code = r.getButton();
                final long time = System.currentTimeMillis() - startTime;
                taskScheduler.addTask(new SchedulingData<>(time, () -> {
                    if (mode == MODE_MOUSE_CLICK_ONLY) {
                        controller.mouse().move(r.getX(), r.getY());
                    }
                    controller.mouse().press(code);
                }));

                for (AbstractSourceGenerator generator : sourceGenerators.values()) {
                    if (mode == MODE_MOUSE_CLICK_ONLY) {
                        generator.submitTask(time, Device.MOUSE, "move", new int[]{r.getX(), r.getY()});
                        generator.submitTask(time + 5, Device.MOUSE, "press", new int[]{code});
                    } else {
                        generator.submitTask(time, Device.MOUSE, "press", new int[]{code});
                    }
                }
                return true;
            }
        });

        mouseListener.setMouseMoved(new Function<>() {
            @Override
            public Boolean apply(final NativeMouseEvent r) {
                if (mode == MODE_MOUSE_CLICK_ONLY) {
                    return true;
                }

                final long time = System.currentTimeMillis() - startTime;
                taskScheduler.addTask(new SchedulingData<>(time, () -> controller.mouse().move(r.getX(), r.getY())));

                for (AbstractSourceGenerator generator : sourceGenerators.values()) {
                    generator.submitTask(time, Device.MOUSE, "move", new int[]{r.getX(), r.getY()});
                }
                return true;
            }
        });
    }

    /**
     * Set the speedup in play back and source code generation for this scheduler.
     * This should be set before replaying.
     *
     * @param speedup
     */
    public void setSpeedup(float speedup) {
        this.speedup = speedup;
    }

    public int getRecordMode() {
        return mode;
    }

    public void setRecordMode(int mode) {
        this.mode = mode;
    }

    public void record() {
        this.startTime = System.currentTimeMillis();
        this.keyListener.startListening();
        this.mouseListener.startListening();
    }

    public void stopRecord() {
        this.keyListener.stopListening();
        this.mouseListener.stopListening();
    }

    public void replay() {
        replay(1, 0, null, 0, true);
    }

    public void replay(long count, long delay, utilities.Function<Void, Void> callBack, long callBackDelay, boolean blocking) {
        long time = taskScheduler.runTasks(count, delay, speedup, callBack, callBackDelay);

        if (blocking && time > 0) {
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopReplay() {
        taskScheduler.halt();
    }

    public void clear() {
        for (AbstractSourceGenerator generator : sourceGenerators.values()) {
            generator.clear();
        }
        taskScheduler.clearTasks();
    }

    public String getGeneratedCode(Language language) {
        AbstractSourceGenerator generator = sourceGenerators.get(language);
        if (generator != null) {
            return generator.getSource(speedup);
        } else { // Return null to indicate generator does not exist
            return null;
        }
    }
}
