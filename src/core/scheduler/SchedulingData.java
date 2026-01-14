package core.scheduler;

public record SchedulingData<T>(long time, T data) {
    public long getTime() {
        return time;
    }

    public T getData() {
        return data;
    }
}