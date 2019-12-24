package dev.jlibra.serialization;

import static java.nio.ByteOrder.LITTLE_ENDIAN;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.List;

import dev.jlibra.KeyUtils;
import dev.jlibra.admissioncontrol.transaction.TransactionArgument;

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

    public Serializer appendPublicKey(PublicKey pubKey) {
        return append(KeyUtils.stripPublicKeyPrefix(ByteSequence.from(pubKey.getEncoded())));
    }

    public Serializer appendWithoutLengthInformation(ByteSequence byteSequence) {
        return append(byteSequence.toArray());
    }

    public Serializer appendTransactionArguments(List<TransactionArgument> transactionArguments) {
        Serializer serializer = append(intToByteArray(transactionArguments.size()));
        for (TransactionArgument arg : transactionArguments) {
            serializer = serializer.append(arg.serialize().toArray());
        }
        return serializer;
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

    public Serializer appendSerializable(LibraSerializable serializable) {
        return append(serializable.serialize().toArray());
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
