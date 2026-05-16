package core.userDefinedTask;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import utilities.DateUtility;
import utilities.json.AutoJsonable;
import utilities.json.IJsonable;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class UsageStatistics implements IJsonable {

    private static final Logger LOGGER = Logger.getLogger(UsageStatistics.class.getName());
    private static final int MAX_EXECUTION_INSTANCES_STORED = 10000;
    private final Map<String, ExecutionInstance> onGoingInstances;
    private long count;
    private Calendar lastUse;
    private Calendar created;
    private long totalExecutionTime;
    private final LinkedList<ExecutionInstance> executionInstances;

    public UsageStatistics() {
        created = Calendar.getInstance();
        onGoingInstances = new HashMap<>();
        executionInstances = new LinkedList<>();
    }

    public static UsageStatistics parseJSON(JsonNode node) {
        try {
            long count = Long.parseLong(node.getNumberValue("count"));
            long totalExecutionTime = Long.parseLong(node.getNumberValue("total_execution_time"));

            Calendar lastUse;
            if (node.isNullableObjectNode("last_use")) {
                lastUse = null;
            } else {
                lastUse = DateUtility.stringToCalendar(node.getStringValue("last_use"));
            }

            Calendar created = DateUtility.stringToCalendar(node.getStringValue("created"));
            if (created == null) {
                LOGGER.warning("Unable to parse created date object.");
                return null;
            }

            UsageStatistics output = new UsageStatistics();
            output.count = count;
            output.totalExecutionTime = totalExecutionTime;
            output.lastUse = lastUse;
            output.created = created;

            return output;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Encountered exception when parsing usage statistics", e);
            return null;
        }
    }

    @Override
    public JsonRootNode jsonize() {
        return JsonNodeFactories.object(JsonNodeFactories.field("count", JsonNodeFactories.number(count)), JsonNodeFactories.field("total_execution_time", JsonNodeFactories.number(totalExecutionTime)), JsonNodeFactories.field("last_use", lastUse != null ? JsonNodeFactories.string(DateUtility.calendarToTimeString(lastUse)) : JsonNodeFactories.nullNode()), JsonNodeFactories.field("created", JsonNodeFactories.string(DateUtility.calendarToTimeString(created))));
    }

    public long getCount() {
        return count;
    }

    public Calendar getLastUse() {
        return lastUse;
    }

    public Calendar getCreated() {
        return created;
    }

    public double getAverageExecutionTime() {
        return (double) totalExecutionTime / count;
    }

    public long getTotalExecutionTime() {
        return totalExecutionTime;
    }

    public List<ExecutionInstance> getExecutionInstances() {
        return Collections.unmodifiableList(executionInstances);
    }

    /**
     * @return an ID to update at completion time.
     */
    public synchronized String useNow(ExecutionContext ignoredExecutionContext) {
        if (lastUse == null) {
            lastUse = Calendar.getInstance();
        } else {
            lastUse.setTimeInMillis(System.currentTimeMillis());
        }

        String id = UUID.randomUUID().toString();
        ExecutionInstance instance = ExecutionInstance.of(System.currentTimeMillis(), ExecutionInstance.DID_NOT_END);
        onGoingInstances.put(id, instance);
        executionInstances.addLast(instance);
        while (executionInstances.size() > MAX_EXECUTION_INSTANCES_STORED) {
            executionInstances.removeFirst();
        }
        return id;
    }

    public void createNow() {
        created.setTimeInMillis(System.currentTimeMillis());
    }

    public synchronized void executionFinished(String id) {
        count++;
        if (!onGoingInstances.containsKey(id)) {
            LOGGER.warning("Unable to find start time for execution statistics " + id);
            return;
        }

        ExecutionInstance instance = onGoingInstances.remove(id);
        long start = instance.getStart();
        long end = System.currentTimeMillis();
        instance.setEnd(end);

        totalExecutionTime += end - start;
    }

    public static final class ExecutionInstance extends AutoJsonable {
        public static final Long DID_NOT_END = -1L;

        private final long start;
        private long end;

        private ExecutionInstance(long start, long end) {
            this.start = start;
            this.end = end;
        }

        private static ExecutionInstance of(long start, long end) {
            return new ExecutionInstance(start, end);
        }

        public long getStart() {
            return start;
        }

        public long getEnd() {
            return end;
        }

        private void setEnd(long end) {
            this.end = end;
        }

        public long getDuration() {
            return end - start;
        }
    }
}
