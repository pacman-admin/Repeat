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
package core.background;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class AbstractBackgroundEntityManager<T> {

    private static final long MAX_TIME_UNUSED_MS = 3600 * 1000;
    private static final long CLEAN_UP_PERIOD_SECOND = 1;

    protected Map<String, T> entities;

    private ScheduledThreadPoolExecutor executor;
    private Map<String, Long> lastUsed;

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

        toRemove.stream().forEach(this::remove);
    }

    public final T get(String id) {
        T output = entities.get(id);
        lastUsed.put(id, System.currentTimeMillis());
        return output;
    }

    public synchronized void remove(String id) {
        lastUsed.remove(id);
        entities.remove(id);
    }
}
