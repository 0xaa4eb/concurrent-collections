package com.tracer.agent;

public interface PerfDataStore {

    void store(TraceRecord measurement);
}
