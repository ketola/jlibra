package dev.jlibra.serialization;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Deserialization {

    public static int readInt(DataInputStream in, int len) {
        byte[] data = readBytes(in, len);
        return ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    public static long readLong(DataInputStream in, int len) {
        byte[] data = readBytes(in, len);
        return ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).getLong();
    }

    public static boolean readBoolean(DataInputStream in) {
        byte[] data = readBytes(in, 1);
        return data[0] == 1;
    }

    public static byte[] readBytes(DataInputStream in, int len) {
        byte[] data = new byte[len];
        try {
            in.read(data);
        } catch (IOException e) {
            throw new RuntimeException("Could not read input stream", e);
        }
        return data;
    }
}
