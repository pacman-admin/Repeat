package core.background;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class AbstractBackgroundEntityManager<T> {

    private static final long MAX_TIME_UNUSED_MS = 3600 * 1000;
    private static final long CLEAN_UP_PERIOD_SECOND = 1;

    protected final Map<String, T> entities;
    private final Map<String, Long> lastUsed;
    private ScheduledThreadPoolExecutor executor;

    protected AbstractBackgroundEntityManager() {
        entities = new HashMap<>();
        lastUsed = new HashMap<>();
    }

    protected final String add(T entity) {
        String id = UUID.randomUUID().toString();

        entities.put(id, entity);
        lastUsed.put(id, System.currentTimeMillis());
        return id;
    }

    public void start() {
        if (executor == null) {
            executor = new ScheduledThreadPoolExecutor(2);
        }
        executor.scheduleWithFixedDelay(this::cleanup, 0, CLEAN_UP_PERIOD_SECOND, TimeUnit.SECONDS);
    }

    public void stop() {
        if (executor == null) {
            return;
        }
        executor.shutdown();
        executor = null;
    }

    private synchronized void cleanup() {
        long now = System.currentTimeMillis();
        List<String> toRemove = new ArrayList<>();

        for (Entry<String, Long> entry : lastUsed.entrySet()) {
            String id = entry.getKey();
            Long lastUsed = entry.getValue();

            if (lastUsed - now > MAX_TIME_UNUSED_MS) {
                toRemove.add(id);
            }
        }

        toRemove.forEach(this::remove);
    }


    public final T get(String id) {
        T output = entities.get(id);
        lastUsed.put(id, System.currentTimeMillis());
        return output;
    }

    public final synchronized void remove(String id) {
        lastUsed.remove(id);
        entities.remove(id);
    }
}
