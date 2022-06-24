package com.tracer.agent;

import com.tracer.agent.util.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public class Settings {

    public static final String EXCLUDE_PACKAGES_PROPERTY = "tracer.exclude-packages";
    public static final String START_PROFILE_METHODS_PROPERTY = "tracer.methods";
    public static final String FILE_PATH_PROPERTY = "tracer.file";
    public static final String AGENT_DISABLED_PROPERTY = "tracer.off";

    private final String file;
    @NotNull
    private final MethodMatcherList tracedMethods;
    private final List<TypeMatcher> tracedTypeMatchers;
    private final PackageList excludedFromInstrumentationPackages;
    private final boolean agentDisabled;

    public Settings(
            String file,
            @NotNull MethodMatcherList tracedMethods,
            PackageList excludedFromInstrumentationPackages,
            boolean agentDisabled) {
        this.tracedMethods = tracedMethods;
        this.tracedTypeMatchers = tracedMethods.getMethods().stream().map(MethodMatcher::getTypeMatcher).collect(Collectors.toList());
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
    public MethodMatcherList getTracedMethods() {
        return tracedMethods;
    }

    public List<TypeMatcher> getTracedTypeMatchers() {
        return tracedTypeMatchers;
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
