package com.tracer.agent;

import com.tracer.agent.util.ByteBuddyMethodResolver;
import com.tracer.agent.util.ByteBuddyTypeConverter;

import com.tracer.agent.util.MethodMatcherList;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.bytecode.assign.Assigner;


/**
 * Allows wiring method id into advice classes like {@link AvgCallTimeProfileAdvice} and others
 * <p>
 * Uses a singleton instance of {@link MethodRepository} to store methods into it.
 */
public class MethodIdFactory implements Advice.OffsetMapping.Factory<MethodId> {

    private final ForMethodIdOffsetMapping instance;

    public MethodIdFactory(MethodRepository methodRepository, MethodMatcherList profileMethods) {
        ByteBuddyMethodResolver byteBuddyMethodResolver = new ByteBuddyMethodResolver(
                profileMethods.useSuperTypes() ? ByteBuddyTypeConverter.SUPER_TYPE_DERIVING_INSTANCE : ByteBuddyTypeConverter.INSTANCE
        );
        this.instance = new ForMethodIdOffsetMapping(byteBuddyMethodResolver, methodRepository);
    }

    @Override
    public Class<MethodId> getAnnotationType() {
        return MethodId.class;
    }

    @Override
    public Advice.OffsetMapping make(ParameterDescription.InDefinedShape target, AnnotationDescription.Loadable<MethodId> annotation, AdviceType adviceType) {
        return instance;
    }

    static class ForMethodIdOffsetMapping implements Advice.OffsetMapping {

        private final ByteBuddyMethodResolver methodResolver;
        private final MethodRepository methodRepository;

        public ForMethodIdOffsetMapping(ByteBuddyMethodResolver methodResolver, MethodRepository methodRepository) {
            this.methodResolver = methodResolver;
            this.methodRepository = methodRepository;
        }

        public Target resolve(TypeDescription instrumentedType,
                              MethodDescription instrumentedMethod,
                              Assigner assigner,
                              Advice.ArgumentHandler argumentHandler,
                              Sort sort) {
            Method method = methodResolver.resolve(instrumentedMethod);
            int id = methodRepository.putAndGetId(method);
            return Target.ForStackManipulation.of(id);
        }
    }
}
