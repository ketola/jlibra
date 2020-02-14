package dev.jlibra.serialization.lcs;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Stream;

import dev.jlibra.LibraRuntimeException;
import dev.jlibra.admissioncontrol.transaction.FixedLengthByteSequence;
import dev.jlibra.admissioncontrol.transaction.ImmutableVariableLengthByteSequence;
import dev.jlibra.admissioncontrol.transaction.VariableLengthByteSequence;
import dev.jlibra.serialization.Serializer;

public class LCSSerializer {

    public VariableLengthByteSequence serialize(Object serializable, Class<?> type) {
        Serializer s = Serializer.builder();

        LCS.ExternallyTaggedEnumeration enumAnnotation = type.getAnnotation(LCS.ExternallyTaggedEnumeration.class);
        if (enumAnnotation != null) {
            s = s.appendInt(enumAnnotation.value());
        }

        List<Method> methods = Stream.of(type.getMethods())
                .filter(m -> m.getAnnotation(LCS.Field.class) != null)
                .sorted((m1, m2) -> m1.getDeclaredAnnotation(LCS.Field.class).value()
                        - m2.getDeclaredAnnotation(LCS.Field.class).value())
                .collect(toList());

        for (Method m : methods) {
            Class<?> returnType = m.getReturnType();

            if (returnType.getAnnotation(LCS.Structure.class) != null
                    || returnType.getAnnotation(LCS.ExternallyTaggedEnumeration.class) != null) {
                Object l = invokeMethod(serializable, m);
                VariableLengthByteSequence value = serialize(l, returnType);
                s = s.appendW(value);
            } else if (returnType.equals(long.class)) {
                long value = (long) invokeMethod(serializable, m);
                s = s.appendLong(value);
            } else if (returnType.equals(FixedLengthByteSequence.class)) {
                FixedLengthByteSequence value = (FixedLengthByteSequence) invokeMethod(serializable, m);
                s = s.append(value);
            } else if (returnType.equals(VariableLengthByteSequence.class)) {
                VariableLengthByteSequence value = (VariableLengthByteSequence) invokeMethod(serializable, m);
                s = s.append(value);
            } else if (returnType.equals(List.class)) {
                List<?> list = (List<?>) invokeMethod(serializable,
                        m);
                s = s.appendInt(list.size());
                for (Object e : list) {
                    s = s.appendW(serialize(e, e.getClass()));
                }
            }
        }

        return ImmutableVariableLengthByteSequence.builder()
                .value(s.toByteSequence())
                .build();
    }

    private Object invokeMethod(Object serializable, Method m) {
        try {
            return m.invoke(serializable);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new LibraRuntimeException("Method invoke failed", e);
        }
    }

}
