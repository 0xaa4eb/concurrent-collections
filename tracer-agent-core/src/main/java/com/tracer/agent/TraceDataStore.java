package com.tracer.agent;

public interface TraceDataStore {

    void store(TraceRecord measurement);
}
