package core.recorder;

public record ReplayConfig(long count, long delay, float speedup) {
    public static ReplayConfig of() {
        return new ReplayConfig(1, 0, 1);
    }

    public static ReplayConfig of(long count, long delay, float speedup) {
        return new ReplayConfig(count, delay, speedup);
    }

    public long getCount() {
        return count;
    }

    public long getDelay() {
        return delay;
    }

    public float getSpeedup() {
        return speedup;
    }
}