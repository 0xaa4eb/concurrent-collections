package com.tracer.agent.util;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import com.tracer.agent.Method;


public class MethodMatcherList implements Iterable<MethodMatcher> {

    private final List<MethodMatcher> methods;

    private MethodMatcherList(List<MethodMatcher> methods) {
        this.methods = methods;
    }

    public static MethodMatcherList parse(String text) {
        return new MethodMatcherList(CommaSeparatedList.parse(text).stream().map(MethodMatcher::parse).collect(Collectors.toList()));
    }

    public boolean anyMatch(Method method) {
        return methods.stream().anyMatch(matcher -> matcher.matches(method));
    }

    @Override
    public String toString() {
        return methods.toString();
    }

    public List<MethodMatcher> getMethods() {
        return methods;
    }

    @NotNull
    @Override
    public Iterator<MethodMatcher> iterator() {
        return methods.iterator();
    }
}
