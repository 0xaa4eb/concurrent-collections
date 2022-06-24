package com.tracer.agent;

import com.tracer.agent.util.CommaSeparatedList;
import com.tracer.agent.util.MethodMatcherList;
import com.tracer.agent.util.PackageList;
import org.jetbrains.annotations.NotNull;

/**
 * Agent settings which define what packages to instrument, at which method profiling should start, etc.
 * It's only possible to set settings via JMV system properties at the time.
 */
public class Settings {

    public static final String EXCLUDE_PACKAGES_PROPERTY = "tracer.exclude-packages";
    public static final String START_PROFILE_METHODS_PROPERTY = "tracer.methods";
    public static final String FILE_PATH_PROPERTY = "tracer.file";
    public static final String AGENT_DISABLED_PROPERTY = "tracer.off";

    private final String file;
    @NotNull
    private final MethodMatcherList methodMatcherList;
    private final PackageList excludedFromInstrumentationPackages;
    private final boolean agentDisabled;

    public Settings(
            String file,
            @NotNull MethodMatcherList methodMatcherList,
            PackageList excludedFromInstrumentationPackages,
            boolean agentDisabled) {
        this.methodMatcherList = methodMatcherList;
        this.file = file;
        this.excludedFromInstrumentationPackages = excludedFromInstrumentationPackages;
        this.agentDisabled = agentDisabled;
    }

    public static Settings fromSystemProperties() {

        String methodsToProfile = System.getProperty(START_PROFILE_METHODS_PROPERTY, "");
        MethodMatcherList profilingStartMethods = MethodMatcherList.parse(methodsToProfile);
        String filePath = System.getProperty(FILE_PATH_PROPERTY);
        PackageList excludedPackages = new PackageList(CommaSeparatedList.parse(System.getProperty(EXCLUDE_PACKAGES_PROPERTY, "")));
        boolean agentDisabled = System.getProperty(AGENT_DISABLED_PROPERTY) != null;

        return new Settings(filePath, profilingStartMethods, excludedPackages, agentDisabled);
    }

    @NotNull
    public MethodMatcherList getProfileMethodList() {
        return methodMatcherList;
    }

    public String getFile() {
        return file;
    }

    public PackageList getExcludedFromInstrumentationPackages() {
        return excludedFromInstrumentationPackages;
    }

    public boolean isAgentDisabled() {
        return agentDisabled;
    }
}
