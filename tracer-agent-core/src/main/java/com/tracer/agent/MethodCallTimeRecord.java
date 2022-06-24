package com.tracer.agent;

public class MethodCallTimeRecord implements TraceRecord {

    private final int methodId;
    private final long millisEpochTime;
    private final long nanos;

    public MethodCallTimeRecord(int methodId, long millisEpochTime, long nanos) {
        this.methodId = methodId;
        this.millisEpochTime = millisEpochTime;
        this.nanos = nanos;
    }

    @Override
    public int getMethodId() {
        return methodId;
    }

    public long getMillisEpochTime() {
        return millisEpochTime;
    }

    public long getNanos() {
        return nanos;
    }
}
