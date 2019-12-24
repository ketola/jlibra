package dev.jlibra.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import dev.jlibra.LibraRuntimeException;

public class Deserialization {

    public static int readInt(InputStream in, int len) {
        byte[] data = readBytes(in, len);
        return ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    public static long readLong(InputStream in, int len) {
        byte[] data = readBytes(in, len);
        return ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getLong();
    }

    public static boolean readBoolean(InputStream in) {
        byte[] data = readBytes(in, 1);
        return data[0] == 1;
    }

    public static ByteSequence readByteSequence(InputStream in, int len) {
        return ByteSequence.from(readBytes(in, len));
    }

    private static byte[] readBytes(InputStream in, int len) {
        byte[] data = new byte[len];
        try {
            in.read(data);
        } catch (IOException e) {
            throw new LibraRuntimeException("Could not read input stream", e);
        }
        return data;
    }
}
