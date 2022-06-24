package com.tracer.agent;

import java.lang.instrument.Instrumentation;

import com.tracer.agent.util.*;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

public class Agent {

    public static void start(String args, Instrumentation instrumentation) {
        // Touch first and initialize shadowed slf4j
        String logLevel = LoggingSettings.getLoggingLevel();
        Settings settings = Settings.fromSystemProperties();
        if (settings.isAgentDisabled()) {
            return;
        }

        if (AgentContext.isLoaded()) {
            return;
        }
        try {
            AgentContext.initInstance(settings);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        AgentContext context = AgentContext.getInstance();

        System.out.println("Tracer agent started, logging level = " + logLevel + ", settings: " + settings);

        MethodMatcherList profileMethodList = settings.getTracedMethods();
        MethodIdFactory methodIdFactory = new MethodIdFactory(context.getMethodRepository(), profileMethodList);

        ElementMatcher.Junction<TypeDescription> ignoreMatcher = buildIgnoreMatcher(settings);
        ElementMatcher<TypeDescription> instrumentationMatcher = buildInstrumentationMatcher(settings);

        AgentBuilder.Identified.Extendable agentBuilder = new AgentBuilder.Default()
                .ignore(ignoreMatcher)
                .type(instrumentationMatcher)
                .transform((builder, typeDescription, classLoader, module, protectionDomain) -> builder.visit(
                        Advice.withCustomMapping()
                                .bind(methodIdFactory)
                                .to(AvgCallTimeProfileAdvice.class)
                                .on(buildMethodsMatcher(settings))
                ));

        AgentBuilder agent = agentBuilder.with(AgentBuilder.TypeStrategy.Default.REDEFINE);

        if (LoggingSettings.TRACE_ENABLED) {
            agent = agent.with(AgentBuilder.Listener.StreamWriting.toSystemOut());
        } else {
            agent = agent.with(new InstrumentationListener());
        }

        agent.installOn(instrumentation);
    }

    private static ElementMatcher.Junction<MethodDescription> buildMethodsMatcher(Settings settings) {
        MethodMatcherList traceMethods = settings.getTracedMethods();
        ByteBuddyMethodResolver byteBuddyMethodResolver = new ByteBuddyMethodResolver(ByteBuddyTypeConverter.SUPER_TYPE_DERIVING_INSTANCE);
        return ElementMatchers.isMethod()
                .and(ElementMatchers.not(ElementMatchers.isAbstract()))
                .and(ElementMatchers.not(ElementMatchers.isConstructor()))
                .and(methodDescription -> traceMethods.anyMatch(byteBuddyMethodResolver.resolve(methodDescription)));
    }

    private static ElementMatcher<TypeDescription> buildInstrumentationMatcher(Settings settings) {
        return target -> {
            Type type = ByteBuddyTypeConverter.SUPER_TYPE_DERIVING_INSTANCE.convert(target.asGenericType());
            return settings.getTracedTypeMatchers().stream().anyMatch(typeMatcher -> typeMatcher.matches(type));
        };
    }

    private static ElementMatcher.Junction<TypeDescription> buildIgnoreMatcher(Settings settings) {
        PackageList excludedPackages = settings.getExcludedFromInstrumentationPackages();

        ElementMatcher.Junction<TypeDescription> ignoreMatcher = ElementMatchers.nameStartsWith("shadowed.")
                .or(ElementMatchers.nameStartsWith("com.tracer"));

        for (String excludedPackage : excludedPackages) {
            ignoreMatcher = ignoreMatcher.or(ElementMatchers.nameStartsWith(excludedPackage));
        }

        return ignoreMatcher;
    }
}
