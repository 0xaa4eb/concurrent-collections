package com.tracer.agent;

import com.tracer.agent.stores.FileTraceDataStore;

public class AgentContext {

    private static AgentContext instance;
    private static volatile boolean agentLoaded = false;

    private final TraceDataStore perfDataStore;
    private final MethodRepository methodRepository;

    private AgentContext(Settings settings) throws Exception {
        this.methodRepository = new MethodRepository();
        this.perfDataStore = new FileTraceDataStore(settings, methodRepository);
    }

    public static AgentContext getInstance() {
        return instance;
    }

    public static void initInstance(Settings settings) throws Exception {
        instance = new AgentContext(settings);
        setLoaded();
    }

    public static boolean isLoaded() {
        return agentLoaded;
    }

    public static void setLoaded() {
        agentLoaded = true;
    }

    public TraceDataStore getPerfDataStore() {
        return perfDataStore;
    }

    public MethodRepository getMethodRepository() {
        return methodRepository;
    }
}
