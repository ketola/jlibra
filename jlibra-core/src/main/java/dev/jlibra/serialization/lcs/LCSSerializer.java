package dev.jlibra.serialization.lcs;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Stream;

import dev.jlibra.LibraRuntimeException;
import dev.jlibra.serialization.ByteArray;
import dev.jlibra.serialization.ByteSequence;
import dev.jlibra.serialization.Serializer;
import dev.jlibra.serialization.lcs.LCS.ExternallyTaggedEnumeration;

public class LCSSerializer {

    private LCSSerializer() {
    }

    public static LCSSerializer create() {
        return new LCSSerializer();
    }

    public ByteArray serialize(Object serializable, Class<?> type) {
        Serializer s = Serializer.builder();

        LCS.ExternallyTaggedEnumeration enumAnnotation = findEnumAnnotation(type);

        if (enumAnnotation != null) {
            int tag = -1;
            for (Class enumType : enumAnnotation.types()) {
                tag++;
                if (enumType.isInstance(serializable)) {
                    type = enumType;
                    break;
                }
            }
            s = s.appendIntAsLeb128(tag);
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
                ByteSequence value = serialize(l, returnType);
                s = s.appendFixedLength(value);
            } else if (returnType.equals(long.class)) {
                long value = (long) invokeMethod(serializable, m);
                s = s.appendLong(value);
            } else if (returnType.equals(int.class)) {
                int value = (int) invokeMethod(serializable, m);
                s = s.appendLong(value);
            } else if (returnType.equals(String.class)) {
                String value = (String) invokeMethod(serializable, m);
                s = s.appendString(value);
            } else if (ByteSequence.class.isAssignableFrom(returnType)) {
                if (m.getAnnotation(LCS.Field.class).fixedLength()) {
                    s = s.appendFixedLength((ByteSequence) invokeMethod(serializable, m));
                } else {
                    s = s.append((ByteSequence) invokeMethod(serializable, m));
                }
            } else if (returnType.equals(List.class)) {
                List<?> list = (List<?>) invokeMethod(serializable,
                        m);
                s = s.appendIntAsLeb128(list.size());
                for (Object e : list) {
                    s = s.appendFixedLength(serialize(e, findAnnotatedClass(e.getClass())));
                }
            } else {
                throw new LibraRuntimeException("Return type " + returnType + " is not recognized for serialization.");
            }
        }
        return s.toByteArray();
    }

    private ExternallyTaggedEnumeration findEnumAnnotation(Class<?> type) {
        ExternallyTaggedEnumeration annotation = type.getAnnotation(LCS.ExternallyTaggedEnumeration.class);
        if (annotation != null) {
            return annotation;
        } else {
            for (Class c : type.getInterfaces()) {
                annotation = (ExternallyTaggedEnumeration) c.getAnnotation(LCS.ExternallyTaggedEnumeration.class);
                if (annotation != null) {
                    return annotation;
                }
            }

        }

        return null;
    }

    private Class findAnnotatedClass(Class e) {
        if (e.getClass().getAnnotation(LCS.Structure.class) != null) {
            return e;
        }

        for (Class c : e.getInterfaces()) {
            if (c.getAnnotation(LCS.Structure.class) != null) {
                return c;
            }
        }
        return e;
    }

    private Object invokeMethod(Object serializable, Method m) {
        try {
            return m.invoke(serializable);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new LibraRuntimeException("Method invoke failed", e);
        }
    }

}
