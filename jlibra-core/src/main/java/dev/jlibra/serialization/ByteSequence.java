package dev.jlibra.serialization;

import java.util.Arrays;

import javax.annotation.concurrent.Immutable;

import org.bouncycastle.util.encoders.Hex;

import com.google.protobuf.ByteString;

@Immutable
public class ByteSequence {

    private final byte[] value;

    protected ByteSequence(byte[] array) {
        byte[] cloned = new byte[array.length];
        System.arraycopy(array, 0, cloned, 0, array.length);
        value = cloned;
    }

    public static ByteSequence from(byte[] array) {
        return new ByteSequence(array);
    }

    public static ByteSequence from(String hexValue) {
        return new ByteSequence(Hex.decode(hexValue));
    }

    public String toString() {
        return Hex.toHexString(value);
    }

    public ByteSequence subseq(int start, int length) {
        byte[] result = new byte[length];
        System.arraycopy(value, start, result, 0, length);
        return new ByteSequence(result);
    }

    public byte[] toArray() {
        byte[] result = new byte[value.length];
        System.arraycopy(value, 0, result, 0, value.length);
        return result;
    }

    public ByteString toByteString() {
        return ByteString.copyFrom(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ByteSequence))
            return false;

        ByteSequence that = (ByteSequence) o;

        return Arrays.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.value);
    }
}
