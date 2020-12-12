package dev.jlibra.serialization.dcs;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import dev.jlibra.DiemRuntimeException;
import dev.jlibra.serialization.ByteArray;
import dev.jlibra.serialization.ByteSequence;
import dev.jlibra.serialization.Serializer;
import dev.jlibra.serialization.dcs.DCS.ExternallyTaggedEnumeration;

public class DCSSerializer {

    private DCSSerializer() {
    }

    public static DCSSerializer create() {
        return new DCSSerializer();
    }

    public ByteArray serialize(Object serializable, Class<?> type) {
        Serializer s = Serializer.builder();

        DCS.ExternallyTaggedEnumeration enumAnnotation = type.getAnnotation(DCS.ExternallyTaggedEnumeration.class);
        if (enumAnnotation != null) {
            List<Class<?>> classes = Arrays.asList(enumAnnotation.classes());
            Class enumMemberType = classes.stream()
                    .filter(c -> serializable.getClass().equals(c) || c.isAssignableFrom(serializable.getClass()))
                    .findFirst()
                    .orElseThrow(() -> new DiemRuntimeException(
                            "Enum membership not found for " + serializable.getClass()));
            s = s.appendIntAsLeb128(classes.indexOf(enumMemberType));
            type = enumMemberType;
        }

        List<Method> methods = Stream.of(type.getMethods())
                .filter(m -> m.getAnnotation(DCS.Field.class) != null)
                .sorted((m1, m2) -> m1.getDeclaredAnnotation(DCS.Field.class).value()
                        - m2.getDeclaredAnnotation(DCS.Field.class).value())
                .collect(toList());

        for (Method m : methods) {
            Class<?> returnType = m.getReturnType();
            if (returnType.getAnnotation(DCS.Structure.class) != null
                    || returnType.getAnnotation(ExternallyTaggedEnumeration.class) != null) {
                Object l = invokeMethod(serializable, m);
                ByteSequence value = serialize(l, returnType);
                s = s.appendFixedLength(value);
            } else if (returnType.equals(long.class)) {
                long value = (long) invokeMethod(serializable, m);
                s = s.appendLong(value);
            } else if (returnType.equals(byte.class)) {
                byte value = (byte) invokeMethod(serializable, m);
                s = s.appendByte(value);
            } else if (returnType.equals(boolean.class)) {
                boolean value = (boolean) invokeMethod(serializable, m);
                s = s.appendBoolean(value);
            } else if (returnType.equals(int.class)) {
                int value = (int) invokeMethod(serializable, m);
                s = s.appendLong(value);
            } else if (returnType.equals(String.class)) {
                String value = (String) invokeMethod(serializable, m);
                s = s.appendString(value);
            } else if (ByteSequence.class.isAssignableFrom(returnType)) {
                if (m.getAnnotation(DCS.Field.class).fixedLength()) {
                    s = s.appendFixedLength((ByteSequence) invokeMethod(serializable, m));
                } else {
                    s = s.append((ByteSequence) invokeMethod(serializable, m));
                }
            } else if (returnType.equals(List.class)) {
                List<?> list = (List<?>) invokeMethod(serializable,
                        m);
                s = s.appendIntAsLeb128(list.size());
                for (Object e : list) {
                    s = s.appendFixedLength(serialize(e,
                            (Class) ((ParameterizedType) m.getGenericReturnType()).getActualTypeArguments()[0]));
                }
            } else {
                throw new DiemRuntimeException("Return type " + returnType + " is not recognized for serialization.");
            }
        }
        return s.toByteArray();
    }

    private Object invokeMethod(Object serializable, Method m) {
        try {
            return m.invoke(serializable);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new DiemRuntimeException("Method invoke failed", e);
        }
    }

}
