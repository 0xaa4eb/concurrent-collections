package com.tracer.agent;

import net.bytebuddy.asm.Advice;

public class AvgCallTimeProfileAdvice {

    @Advice.OnMethodEnter
    static void enter(
            @Advice.Local("timestamp") long timestamp,
            @Advice.Local("epochTimestamp") long epochTimestamp) {
        timestamp = System.nanoTime();
    }

    @Advice.OnMethodExit
    static void exit(
            @MethodId int methodId,
            @Advice.Local("timestamp") long timestamp,
            @Advice.Local("epochTimestamp") long epochTimestamp) {
        long durationNanos = System.nanoTime() - timestamp;

        AgentContext.getInstance().getPerfDataStore().store(new MethodCallTimeRecord(methodId, epochTimestamp, durationNanos));
    }
}
