package dev.jlibra.serialization.lcs;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Stream;

import dev.jlibra.AccountAddress;
import dev.jlibra.LibraRuntimeException;
import dev.jlibra.serialization.ByteSequence;
import dev.jlibra.serialization.LibraSerializable;
import dev.jlibra.serialization.Serializer;

public class LCSSerializer {

    public ByteSequence serialize(LibraSerializable serializable, Class<?> type) {
        Serializer s = Serializer.builder();

        LCS.Enum enumAnnotation = type.getAnnotation(LCS.Enum.class);
        if (enumAnnotation != null) {
            s = s.appendInt(enumAnnotation.ordinal());
        }

        List<Method> methods = Stream.of(type.getMethods())
                .filter(m -> m.getAnnotation(LCS.Field.class) != null)
                .sorted((m1, m2) -> m1.getDeclaredAnnotation(LCS.Field.class).ordinal()
                        - m2.getDeclaredAnnotation(LCS.Field.class).ordinal())
                .collect(toList());

        for (Method m : methods) {
            Class<?> returnType = m.getReturnType();

            if (returnType.equals(AccountAddress.class)) {
                AccountAddress value = (AccountAddress) invokeMethod(serializable, m);
                s = s.appendWithoutLengthInformation(value.getByteSequence());
            } else if (returnType.equals(long.class)) {
                long value = (long) invokeMethod(serializable, m);
                s = s.appendLong(value);
            } else if (LibraSerializable.class.isAssignableFrom(returnType)) {
                LibraSerializable l = (LibraSerializable) invokeMethod(serializable, m);
                ByteSequence value = serialize(l, returnType);
                s = s.appendWithoutLengthInformation(value);
            } else if (returnType.equals(ByteSequence.class)) {
                ByteSequence value = (ByteSequence) invokeMethod(serializable, m);
                s = s.append(value);
            } else if (returnType.equals(List.class)) {
                @SuppressWarnings("unchecked")
                List<? extends LibraSerializable> list = (List<? extends LibraSerializable>) invokeMethod(serializable,
                        m);
                s = s.appendInt(list.size());
                for (LibraSerializable e : list) {
                    s = s.appendWithoutLengthInformation(serialize(e, e.getClass()));
                }
            }
        }

        return s.toByteSequence();
    }

    private Object invokeMethod(LibraSerializable serializable, Method m) {
        try {
            return m.invoke(serializable);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new LibraRuntimeException("Method invoke failed", e);
        }
    }

}
