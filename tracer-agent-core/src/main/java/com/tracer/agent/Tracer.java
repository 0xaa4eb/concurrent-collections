package com.tracer.agent;

public interface Tracer {

    void record(TraceRecord traceRecord);

    void report();
}
