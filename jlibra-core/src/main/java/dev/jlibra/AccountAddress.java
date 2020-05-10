package dev.jlibra;

import dev.jlibra.serialization.ByteArray;
import dev.jlibra.serialization.ByteSequence;

/**
 * Account address is used to identify the target and source accounts in
 * transactions. Account address is calculated from a {@link AuthenticationKey}
 * by taking the last 16 bytes of the authentication key.
 */
public class AccountAddress implements ByteSequence {

    private ByteArray bytes;

    private AccountAddress(ByteArray bytes) {
        this.bytes = bytes;
    }

    public static AccountAddress fromAuthenticationKey(AuthenticationKey authenticationKey) {
        return new AccountAddress(
                authenticationKey.toByteArray().subseq(16, 16));
    }

    public static AccountAddress fromByteArray(ByteArray bytes) {
        return new AccountAddress(bytes);
    }

    public static AccountAddress fromHexString(String hexString) {
        return new AccountAddress(ByteArray.from(hexString));
    }

    @Override
    public byte[] toArray() {
        return bytes.toArray();
    }

    @Override
    public String toString() {
        return bytes.toString();
    }

}
