package dev.jlibra.serialization;

import static java.nio.ByteOrder.LITTLE_ENDIAN;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Serializer {

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
        return append(intToByteArray(byteArray.length))
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

    public Serializer appendByte(byte i) {
        return append(new byte[] { i });
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

    private Serializer append(byte[] b) {
        byte[] newBytes = new byte[bytes.length + b.length];
        System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
        System.arraycopy(b, 0, newBytes, bytes.length, b.length);
        return new Serializer(newBytes);
    }

    public ByteSequence toByteSequence() {
        return ByteSequence.from(bytes);
    }
}
