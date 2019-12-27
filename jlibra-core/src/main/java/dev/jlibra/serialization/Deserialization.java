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
        try {
            int available = in.available();
            if (available < len) {
                throw new LibraRuntimeException(String.format(
                        "Message is not long enough (%d) to read %d bytes from it. It could mean the message is corrupted or different format than expected.",
                        available, len));
            }

            byte[] data = new byte[len];
            in.read(data);
            return data;
        } catch (IOException e) {
            throw new LibraRuntimeException("Could not read input stream", e);
        }
    }
}
