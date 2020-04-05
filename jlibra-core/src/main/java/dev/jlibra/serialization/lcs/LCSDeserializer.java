package dev.jlibra.serialization.lcs;

import static dev.jlibra.serialization.Deserialization.readBoolean;
import static dev.jlibra.serialization.Deserialization.readByteArray;
import static dev.jlibra.serialization.Deserialization.readInt;
import static dev.jlibra.serialization.Deserialization.readLong;
import static dev.jlibra.serialization.Deserialization.readString;
import static dev.jlibra.serialization.Deserialization.readUleb128Int;
import static java.util.stream.Collectors.toList;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import dev.jlibra.AccountAddress;
import dev.jlibra.LibraRuntimeException;
import dev.jlibra.serialization.ByteArray;
import dev.jlibra.serialization.Deserialization;

public class LCSDeserializer<T> {

    public T deserialize(ByteArray bytes, Class<T> returnType) {
        Object builderInstance = getBuilderForType(returnType);

        try (DataInputStream in = new DataInputStream(
                new ByteArrayInputStream(bytes.toArray()))) {

            Object builtBuilder = deserialize(in, builderInstance, returnType);
            return (T) buildType(builtBuilder);
        } catch (IOException e) {
            throw new LibraRuntimeException("Deserialization failed", e);
        }
    }

    private Object deserialize(DataInputStream inputStream, Object builder, Class<T> returnType) {
        List<Method> methods = Stream.of(returnType.getMethods())
                .filter(m -> m.getAnnotation(LCS.Field.class) != null)
                .sorted((m1, m2) -> m1.getDeclaredAnnotation(LCS.Field.class).value()
                        - m2.getDeclaredAnnotation(LCS.Field.class).value())
                .collect(toList());

        for (Method m : methods) {
            if (m.getReturnType().equals(ByteArray.class)) {
                int addressLength = readUleb128Int(inputStream);
                invokeMethod(builder, m, readByteArray(inputStream, addressLength));
            } else if (m.getReturnType().equals(AccountAddress.class)) {
                invokeMethod(builder, m, AccountAddress.fromByteArray(readByteArray(inputStream, 16)));
            } else if (m.getReturnType().equals(String.class)) {
                int stringLength = readUleb128Int(inputStream);
                invokeMethod(builder, m, readString(inputStream, stringLength));
            } else if (m.getReturnType().equals(long.class)) {
                invokeMethod(builder, m, readLong(inputStream, 8));
            } else if (m.getReturnType().equals(boolean.class)) {
                invokeMethod(builder, m, readBoolean(inputStream));
            } else if (m.getReturnType().equals(int.class)) {
                invokeMethod(builder, m, readInt(inputStream, 4));
            } else if (m.getReturnType().equals(List.class)) {
                int listSize = readUleb128Int(inputStream);
                List list = new ArrayList<>();
                if (listSize != 0) {
                    Class<?> listContentType = (Class<?>) ((ParameterizedType) m.getGenericReturnType())
                            .getActualTypeArguments()[0];
                    Class[] types = listContentType.getAnnotation(LCS.ExternallyTaggedEnumeration.class).types();

                    for (int i = 0; i < listSize; i++) {
                        int typeIdentifier = Deserialization.readUleb128Int(inputStream);
                        Class type = types[typeIdentifier];
                        list.add(buildType(new LCSDeserializer().deserialize(inputStream,
                                getBuilderForType(type),
                                type)));
                    }
                }
                invokeMethod(builder, m, list);
            } else if (m.getReturnType().getAnnotation(LCS.Structure.class) != null) {
                Object builtBuilderForType = new LCSDeserializer().deserialize(inputStream,
                        getBuilderForType(m.getReturnType()), m.getReturnType());
                invokeMethod(builder, m, buildType(builtBuilderForType));
            } else if (m.getReturnType().getAnnotation(LCS.ExternallyTaggedEnumeration.class) != null) {
                Class[] types = m.getReturnType().getAnnotation(LCS.ExternallyTaggedEnumeration.class).types();
                int typeIdentifier = Deserialization.readUleb128Int(inputStream);
                Class type = types[typeIdentifier];
                Object builtBuilderForType = new LCSDeserializer().deserialize(inputStream,
                        getBuilderForType(type), type);
                invokeMethod(builder, m, buildType(builtBuilderForType));
            }
        }
        return builder;
    }

    private Object buildType(Object builderForType) {
        try {
            return builderForType.getClass().getMethod("build", null).invoke(builderForType, null);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException e) {
            throw new LibraRuntimeException("Could not build type ", e);
        }
    }

    private Object getBuilderForType(Class<?> libraTypeClass) {
        try {
            return libraTypeClass.getAnnotation(LCS.Structure.class).builderClass()
                    .getMethod("builder", null).invoke(null, null);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException e) {
            throw new LibraRuntimeException("Could not get builder ", e);
        }
    }

    private Object invokeMethod(Object builder, Method m, Object parameter) {
        try {
            return getMethodFromBuilderClass(builder.getClass(), m).invoke(builder, parameter);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new LibraRuntimeException("Could not invoke method on Builder", e);
        }
    }

    private Method getMethodFromBuilderClass(Class<?> builderClass, Method m) {
        try {
            return builderClass.getMethod(m.getName(), convert(m.getReturnType()));
        } catch (NoSuchMethodException | SecurityException e) {
            throw new LibraRuntimeException("Could not get method from Builder", e);
        }
    }

    private Class convert(Class c) {
        if (c == List.class) {
            return Iterable.class;
        }
        return c;
    }

}
