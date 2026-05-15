package core.webui.server.handlers.renderedobjects;

import argo.jdom.JsonNode;
import argo.jdom.JsonNodeFactories;
import core.userDefinedTask.UsageStatistics;
import utilities.DateUtility;
import utilities.json.IJsonable;
import utilities.json.JSONUtility;

import java.awt.*;
import java.util.Base64;
import java.util.stream.Collectors;

public final class RenderedUserDefinedActionStatistics {

    private static final int MAX_EXECUTION_INSTANCES = 100;
    private String created;
    private String lastUsed;
    private String totalExecutionTime;
    private String averageExecutionTime;
    private String encodedTaskActivationBreakdown = "";
    private String encodedTaskExecutionInstances;

    private RenderedUserDefinedActionStatistics() {
    }

    public static RenderedUserDefinedActionStatistics fromUserDefinedActionStatistics(UsageStatistics statistics) {
        RenderedUserDefinedActionStatistics result = new RenderedUserDefinedActionStatistics();
        result.created = DateUtility.calendarToTimeString(statistics.getCreated());
        result.lastUsed = statistics.getLastUse() == null ? "Never" : DateUtility.calendarToTimeString(statistics.getLastUse());
        result.totalExecutionTime = DateUtility.durationToString(statistics.getTotalExecutionTime());
        result.averageExecutionTime = DateUtility.durationToString(Math.round(statistics.getAverageExecutionTime()));

        JsonNode executionInstancesNode = JsonNodeFactories.object(
                JsonNodeFactories.field("executionInstances", JsonNodeFactories.array(statistics.getExecutionInstances().stream()
                        .skip(Math.max(0, statistics.getExecutionInstances().size() - MAX_EXECUTION_INSTANCES))
                        .map(RenderedUserDefinedActionExecutionInstance::fromExecutionInstance)
                        .map(IJsonable::jsonize)
                        .collect(Collectors.toList()))));
        result.encodedTaskExecutionInstances = Base64.getEncoder().encodeToString(JSONUtility.jsonToString(executionInstancesNode).getBytes());
        return result;
    }

    private static String formatColor(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(String lastUsed) {
        this.lastUsed = lastUsed;
    }

    public String getTotalExecutionTime() {
        return totalExecutionTime;
    }

    public void setTotalExecutionTime(String totalExecutionTime) {
        this.totalExecutionTime = totalExecutionTime;
    }

    public String getAverageExecutionTime() {
        return averageExecutionTime;
    }

    public void setAverageExecutionTime(String averageExecutionTime) {
        this.averageExecutionTime = averageExecutionTime;
    }

    public String getEncodedTaskActivationBreakdown() {
        return encodedTaskActivationBreakdown;
    }

    public void setEncodedTaskActivationBreakdown(String encodedTaskActivationBreakdown) {
        this.encodedTaskActivationBreakdown = encodedTaskActivationBreakdown;
    }

    public String getEncodedTaskExecutionInstances() {
        return encodedTaskExecutionInstances;
    }

    public void setEncodedTaskExecutionInstances(String encodedTaskExecutionInstances) {
        this.encodedTaskExecutionInstances = encodedTaskExecutionInstances;
    }

    private static final class BreakdownPieChartEntry {
        String name;
        String color;
        long data;

        private static BreakdownPieChartEntry of(String name, String color, long data) {
            BreakdownPieChartEntry result = new BreakdownPieChartEntry();
            result.name = name.isBlank() ? "Empty" : name;
            result.color = color;
            result.data = data;
            return result;
        }
    }
}
