package dev.jlibra.serialization;

import static java.nio.ByteOrder.LITTLE_ENDIAN;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;

import org.bouncycastle.util.encoders.Hex;

import dev.jlibra.admissioncontrol.transaction.TransactionArgument;

public class CanonicalSerialization {

    public static byte[] serializeByteArray(byte[] bytes) {
        return join(serializeInt(bytes.length), bytes);
    }

    public static byte[] serializeTransactionArguments(List<TransactionArgument> transactionArguments) {
        byte[] result = new byte[0];

        // put the size of the array in beginning
        result = join(result, serializeInt(transactionArguments.size()));

        for (TransactionArgument arg : transactionArguments) {
            result = join(result, arg.serialize());
        }
        return result;
    }

    public static byte[] serializeString(String str) {
        byte[] bytes = str.getBytes(Charset.forName("UTF-8"));
        return serializeByteArray(bytes);
    }

    public static byte[] serializeLong(long l) {
        return longToByteArray(l);
    }

    public static byte[] serializeInt(int i) {
        return intToByteArray(i);
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

    public static byte[] join(byte[] a, byte[] b) {
        System.out.println(Hex.toHexString(b));
        byte[] ab = new byte[a.length + b.length];
        System.arraycopy(a, 0, ab, 0, a.length);
        System.arraycopy(b, 0, ab, a.length, b.length);
        return ab;
    }

}
