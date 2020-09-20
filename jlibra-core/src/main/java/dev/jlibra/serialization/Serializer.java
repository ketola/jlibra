package dev.jlibra.serialization;

import static java.nio.ByteOrder.LITTLE_ENDIAN;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Serializer {

    private final static int MASK_DATA = 0x7f;
    private final static int MASK_CONTINUE = 0x80;

    private byte[] bytes;

    private Serializer(byte[] bytes) {
        this.bytes = bytes;
    }

    public static Serializer builder() {
        return new Serializer(new byte[0]);
    }

    public Serializer append(ByteSequence byteSequence) {
        return appendByteArray(byteSequence.toArray());
    }

    private Serializer appendByteArray(byte[] byteArray) {
        return append(intToLeb128ByteArray(byteArray.length))
                .append(byteArray);
    }

    public Serializer appendFixedLength(ByteSequence byteSequence) {
        return append(byteSequence.toArray());
    }

    public Serializer appendString(String str) {
        return appendByteArray(str.getBytes(StandardCharsets.UTF_8));
    }

    public Serializer appendLong(long l) {
        return append(longToByteArray(l));
    }

    public Serializer appendInt(int i) {
        return append(intToByteArray(i));
    }

    public Serializer appendIntAsLeb128(int i) {
        return append(intToLeb128ByteArray(i));
    }

    public Serializer appendShort(short i) {
        return append(shortToByteArray(i));
    }

    public Serializer appendByte(byte i) {
        return append(new byte[] { i });
    }

    public Serializer appendBoolean(boolean i) {
        return append(new byte[] { (byte) (i ? 1 : 0) });
    }

    private static byte[] shortToByteArray(short i) {
        return ByteBuffer.allocate(Short.BYTES)
                .order(LITTLE_ENDIAN).putShort(i)
                .order(LITTLE_ENDIAN).array();
    }

    private static byte[] intToByteArray(int i) {
        return ByteBuffer.allocate(Integer.BYTES)
                .order(LITTLE_ENDIAN).putInt(i)
                .order(LITTLE_ENDIAN).array();
    }

    private static byte[] longToByteArray(long l) {
        return ByteBuffer.allocate(Long.BYTES)
                .order(LITTLE_ENDIAN).putLong(l)
                .order(LITTLE_ENDIAN).array();
    }

    /**
     * The uleb 128 encoding is a way of efficiently storing integers. For an
     * explanation, see: https://en.wikipedia.org/wiki/LEB128 Thanks to libosu
     * project for the implementation used here:
     * https://github.com/zcd/libosu/blob/master/src/main/java/com/zerocooldown/libosu/util/Uleb128.java
     */
    private static byte[] intToLeb128ByteArray(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("The value to serialize cannot be negative");
        }

        List<Byte> bytes = new ArrayList<>();
        do {
            byte b = (byte) (i & MASK_DATA);
            i >>= 7;
            if (i != 0) {
                b |= MASK_CONTINUE;
            }
            bytes.add(b);
        } while (i != 0);

        byte[] ret = new byte[bytes.size()];
        for (int j = 0; j < bytes.size(); j++) {
            ret[j] = bytes.get(j);
        }
        return ret;
    }

    private Serializer append(byte[] b) {
        byte[] newBytes = new byte[bytes.length + b.length];
        System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
        System.arraycopy(b, 0, newBytes, bytes.length, b.length);
        return new Serializer(newBytes);
    }

    public ByteArray toByteArray() {
        return ByteArray.from(bytes);
    }
}
