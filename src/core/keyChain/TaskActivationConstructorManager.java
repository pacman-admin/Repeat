package core.keyChain;

import core.background.AbstractBackgroundEntityManager;
import org.simplenativehooks.NativeKeyHook;
import org.simplenativehooks.listeners.AbstractGlobalKeyListener;

public final class TaskActivationConstructorManager extends AbstractBackgroundEntityManager<TaskActivationConstructor> {

    private final AbstractGlobalKeyListener keyListener;

    public TaskActivationConstructorManager() {
        keyListener = NativeKeyHook.of();
    }

    @Override
    public void start() {
        super.start();

        keyListener.setKeyReleased(r -> {
            onStroke(KeyStroke.of(r));
            return true;
        });

        keyListener.startListening();
    }

    @Override
    public void stop() {
        keyListener.stopListening();
        super.stop();
    }

    private synchronized void onStroke(KeyStroke stroke) {
        for (TaskActivationConstructor constructor : entities.values()) {
            constructor.onStroke(stroke);
        }
    }

    public synchronized String addNewConstructor(ActionInvoker source) {
        return addNewConstructor(source, TaskActivationConstructor.Config.of());
    }

    public synchronized String addNewConstructor(ActionInvoker source, TaskActivationConstructor.Config config) {
        TaskActivationConstructor constructor = new TaskActivationConstructor(source, config);
        return add(constructor);
    }
}