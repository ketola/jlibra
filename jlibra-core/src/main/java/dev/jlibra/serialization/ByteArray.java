package dev.jlibra.serialization;

import java.util.Arrays;

import org.bouncycastle.util.encoders.Hex;

public class ByteArray implements ByteSequence {

    private final byte[] value;

    protected ByteArray(byte[] array) {
        byte[] cloned = new byte[array.length];
        System.arraycopy(array, 0, cloned, 0, array.length);
        value = cloned;
    }

    public static ByteArray from(byte[] array) {
        return new ByteArray(array);
    }

    public static ByteArray from(String hexValue) {
        return new ByteArray(Hex.decode(hexValue));
    }

    public String toString() {
        return Hex.toHexString(value);
    }

    public ByteArray subseq(int start, int length) {
        byte[] result = new byte[length];
        System.arraycopy(value, start, result, 0, length);
        return new ByteArray(result);
    }

    @Override
    public byte[] toArray() {
        byte[] result = new byte[value.length];
        System.arraycopy(value, 0, result, 0, value.length);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ByteArray))
            return false;

        ByteArray that = (ByteArray) o;

        return Arrays.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.value);
    }
}
