package core.userDefinedTask;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import argo.jdom.JsonRootNode;
import core.keyChain.ActionInvoker;
import utilities.DateUtility;
import utilities.json.AutoJsonable;
import utilities.json.IJsonable;
import utilities.json.JSONUtility;
import utilities.json.Jsonizer;

public class UsageStatistics implements IJsonable {

	private static final Logger LOGGER = Logger.getLogger(UsageStatistics.class.getName());
	private static final int MAX_EXECUTION_INSTANCES_STORED = 10000;

	private long count;
	private Calendar lastUse;
	private Calendar created;
	private long totalExecutionTime;
	private Map<ActionInvoker, Long> taskActivationBreakdown;

	private Map<String, ExecutionInstance> onGoingInstances;
	private LinkedList<ExecutionInstance> executionInstances;

	public UsageStatistics() {
		created = Calendar.getInstance();
		taskActivationBreakdown = new HashMap<>();
		onGoingInstances = new HashMap<>();
		executionInstances = new LinkedList<>();
	}

	@Override
	public JsonRootNode jsonize() {
		return JsonNodeFactories.object(
				JsonNodeFactories.field("count", JsonNodeFactories.number(count)),
				JsonNodeFactories.field("total_execution_time", JsonNodeFactories.number(totalExecutionTime)),
				JsonNodeFactories.field("last_use", lastUse != null ? JsonNodeFactories.string(DateUtility.calendarToTimeString(lastUse)) : JsonNodeFactories.nullNode()),
				JsonNodeFactories.field("created", JsonNodeFactories.string(DateUtility.calendarToTimeString(created))),
				JsonNodeFactories.field("task_activations_breakdown", JsonNodeFactories.array(
						taskActivationBreakdown.entrySet().stream().map(
								e -> JsonNodeFactories.object(
										JsonNodeFactories.field("task_activation", e.getKey().jsonize()),
										JsonNodeFactories.field("count", JsonNodeFactories.number(e.getValue())))
								).collect(Collectors.toList())
						)),
				JsonNodeFactories.field("execution_instances", JsonNodeFactories.array(JSONUtility.listToJson(executionInstances)))
				);
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

			Map<ActionInvoker, Long> taskActivationBreakdown = new HashMap<>();
			if (node.isArrayNode("task_activations_breakdown")) {
				List<JsonNode> nodes = node.getArrayNode("task_activations_breakdown");
				for (JsonNode n : nodes) {
					JsonNode activationNode = n.getNode("task_activation");
					ActionInvoker activation = ActionInvoker.parseJSON(activationNode);
					if (activation == null) {
						LOGGER.warning("Unable to parse task activation.");
						return null;
					}

					long activationCount = Long.parseLong(n.getNode("count").getNumberValue());
					taskActivationBreakdown.put(activation, activationCount);
				}
			}

			LinkedList<ExecutionInstance> instances = new LinkedList<>();
			if (node.isArrayNode("execution_instances")) {
				List<JsonNode> nodes = node.getArrayNode("execution_instances");
				instances = nodes.stream().map(n -> {
					ExecutionInstance i = ExecutionInstance.of(0, 0);
					Jsonizer.parse(n, i);
					return i;
				}).collect(Collectors.toCollection(LinkedList::new));
			}

			UsageStatistics output = new UsageStatistics();
			output.count = count;
			output.totalExecutionTime = totalExecutionTime;
			output.lastUse = lastUse;
			output.created = created;
			output.taskActivationBreakdown = taskActivationBreakdown;
			output.executionInstances = instances;

			return output;
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Encountered exception when parsing usage statistics", e);
			return null;
		}
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

	public Map<ActionInvoker, Long> getTaskActivationBreakdown() {
		return Collections.unmodifiableMap(taskActivationBreakdown);
	}

	public List<ExecutionInstance> getExecutionInstances() {
		return Collections.unmodifiableList(executionInstances);
	}

	/**
	 * @return an ID to update at completion time.
	 */
	public synchronized String useNow(ExecutionContext executionContext) {
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
		long countForActivation = taskActivationBreakdown.getOrDefault(executionContext.getActivation(), 0L);
		taskActivationBreakdown.put(executionContext.getActivation(), countForActivation + 1);
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

		totalExecutionTime += end-start;
	}

	public static class ExecutionInstance extends AutoJsonable {
		public static final Long DID_NOT_END = -1L;

		private long start;
		private long end;

		static ExecutionInstance of(long start, long end) {
			return new ExecutionInstance(start, end);
		}

		private ExecutionInstance(long start, long end) {
			this.start = start;
			this.end = end;
		}

		public long getStart() {
			return start;
		}

		public long getEnd() {
			return end;
		}

		public long getDuration() {
			return end - start;
		}

		private void setEnd(long end) {
			this.end = end;
		}
	}
}
