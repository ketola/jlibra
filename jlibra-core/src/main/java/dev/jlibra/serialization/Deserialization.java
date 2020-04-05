package dev.jlibra.serialization;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import dev.jlibra.LibraRuntimeException;

public class Deserialization {

    private final static int BITS_LONG = 64;
    private final static int MASK_DATA = 0x7f;
    private final static int MASK_CONTINUE = 0x80;

    public static int readInt(InputStream in, int len) {
        byte[] data = readBytes(in, len);
        return ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    public static int readUleb128Int(InputStream in) {
        int value = 0;
        int bitSize = 0;
        int read;

        do {
            try {
                if ((read = in.read()) == -1) {
                    throw new LibraRuntimeException("Unexpected EOF while parsing ULEB 128");
                }
            } catch (IOException e) {
                throw new LibraRuntimeException("Could not parse ULEB 128 integer from bytes stream", e);
            }

            value += (read & MASK_DATA) << bitSize;
            bitSize += 7;
            if (bitSize >= BITS_LONG) {
                throw new ArithmeticException("ULEB128 value exceeds maximum value for long type.");
            }

        } while ((read & MASK_CONTINUE) != 0);
        return value;
    }

    public static long readLong(InputStream in, int len) {
        byte[] data = readBytes(in, len);
        return ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getLong();
    }

    public static boolean readBoolean(InputStream in) {
        byte[] data = readBytes(in, 1);
        return data[0] == 1;
    }

    public static ByteArray readByteArray(InputStream in, int len) {
        return ByteArray.from(readBytes(in, len));
    }

    public static String readString(InputStream in, int len) {
        return new String(readBytes(in, len), UTF_8);
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
