package com.tracer.agent.util;

import com.tracer.agent.Method;
import com.tracer.agent.Type;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.description.method.MethodDescription;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Converts byte buddy method description to internal domain class {@link Method}
 */
@Slf4j
public class ByteBuddyMethodResolver {

    private static final AtomicLong idGenerator = new AtomicLong();

    private final ByteBuddyTypeConverter declaringTypeConverter;

    public ByteBuddyMethodResolver(ByteBuddyTypeConverter declaringTypeConverter) {
        this.declaringTypeConverter = declaringTypeConverter;
    }

    public Method resolve(MethodDescription description) {
        Type declaringType = declaringTypeConverter.convert(description.getDeclaringType().asGenericType());

        String actualName = description.getActualName();
        String name;
        if (description.isConstructor()) {
            name = "<init>";
        } else if (actualName.isEmpty() && description.isStatic()) {
            name = "<clinit>";
        } else {
            name = description.getActualName();
        }

        Method resolved = Method.builder()
                .id(idGenerator.incrementAndGet())
                .name(name)
                .declaringType(declaringType)
                .build();

        if (LoggingSettings.TRACE_ENABLED) {
            log.trace("Resolved {} to {}", description, resolved);
        }
        return resolved;
    }
}
