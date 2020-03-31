package dev.jlibra;

import com.google.protobuf.ByteString;

import dev.jlibra.serialization.ByteArray;
import dev.jlibra.serialization.ByteSequence;

public class AccountAddress implements ByteSequence {

    private ByteArray bytes;

    private AccountAddress(ByteArray bytes) {
        this.bytes = bytes;
    }

    // public static AccountAddress
    // fromAuthenticationKeyPreimage(AuthenticationKeyPreimage
    // authenticationKeyPreimage) {
    // return new AccountAddress(
    // authenticationKeyPreimage.toByteArray().subseq(16, 16));
    // }

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
    public ByteString toByteString() {
        return bytes.toByteString();
    }

    @Override
    public String toString() {
        return bytes.toString();
    }

}
