package core.userDefinedTask.internals;

public class ActionExecutionRequest {
    private int repeatCount;
    private long delayMsBetweenRepeat;
    //private TaskActivation activation;

    private ActionExecutionRequest() {
    }

    public static ActionExecutionRequest of() {
        return of(1, 0);
    }

    public static ActionExecutionRequest of(int repeatCount, long delay) {
        ActionExecutionRequest result = new ActionExecutionRequest();
        result.repeatCount = repeatCount;
        result.delayMsBetweenRepeat = delay;
        //result.activation = TaskActivation.newBuilder().build();
        return result;
    }
    public int getRepeatCount() {
        return repeatCount;
    }
    public long getDelayMsBetweenRepeat() {
        return delayMsBetweenRepeat;
    }
}
