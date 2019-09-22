package dev.jlibra.serialization;

import static java.nio.ByteOrder.LITTLE_ENDIAN;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;

import dev.jlibra.admissioncontrol.transaction.TransactionArgument;

public class Serializer {

    private byte[] bytes;

    private Serializer(byte[] bytes) {
        this.bytes = bytes;
    }

    public static Serializer builder() {
        return new Serializer(new byte[0]);
    }

    public Serializer appendByteArray(byte[] byteArray) {
        return append(intToByteArray(byteArray.length))
                .append(byteArray);
    }

    public Serializer appendTransactionArguments(List<TransactionArgument> transactionArguments) {
        append(intToByteArray(transactionArguments.size()));
        for (TransactionArgument arg : transactionArguments) {
            append(arg.serialize());
        }
        return this;
    }

    public Serializer appendString(String str) {
        return appendByteArray(str.getBytes(Charset.forName("UTF-8")));
    }

    public Serializer appendLong(long l) {
        return append(longToByteArray(l));
    }

    public Serializer appendInt(int i) {
        return append(intToByteArray(i));
    }

    public Serializer appendSerializable(LibraSerializable serializable) {
        return append(serializable.serialize());
    }

    private static byte[] intToByteArray(int i) {
        return ByteBuffer.allocate(Integer.BYTES).order(LITTLE_ENDIAN).putInt(i)
                .order(LITTLE_ENDIAN).array();
    }

    private static byte[] longToByteArray(long l) {
        byte[] array = ByteBuffer.allocate(Long.BYTES).order(LITTLE_ENDIAN).putLong(l)
                .order(LITTLE_ENDIAN).array();
        return array;
    }

    private Serializer append(byte[] b) {
        byte[] ab = new byte[bytes.length + b.length];
        System.arraycopy(bytes, 0, ab, 0, bytes.length);
        System.arraycopy(b, 0, ab, bytes.length, b.length);
        this.bytes = ab;
        return this;
    }

    public byte[] toByteArray() {
        return bytes;
    }

}
