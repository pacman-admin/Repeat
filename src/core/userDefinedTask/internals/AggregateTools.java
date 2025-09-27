package core.userDefinedTask.internals;

import java.util.Arrays;
import java.util.Collection;

public record AggregateTools(Collection<ITools> tools) implements ITools {

    public static AggregateTools of(Collection<ITools> tools) {
        if (tools.isEmpty()) {
            return new AggregateTools(Arrays.asList(NoopTools.of()));
        }
        return new AggregateTools(tools);
    }

    /**
     * Returns the first clipboard.
     */
    @Override
    public String getClipboard() {
        return tools.iterator().next().getClipboard();
    }

    @Override
    public boolean setClipboard(String data) {
        boolean result = true;
        for (ITools tool : tools) {
            result &= tool.setClipboard(data);
        }
        return result;
    }
}