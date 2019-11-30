package dev.jlibra;

import java.security.PublicKey;

import org.bouncycastle.util.encoders.Hex;

public class AccountAddress {

    private byte[] address;

    private AccountAddress(byte[] address) {
        this.address = address;
    }

    public byte[] asByteArray() {
        byte[] clone = new byte[address.length];
        System.arraycopy(address, 0, clone, 0, address.length);
        return clone;
    }

    public String asHexString() {
        return Hex.toHexString(address);
    }

    public static AccountAddress ofPublicKey(PublicKey publicKey) {
        return new AccountAddress(KeyUtils.toByteArrayLibraAddress(publicKey.getEncoded()));
    }

    public static AccountAddress ofByteArray(byte[] address) {
        return new AccountAddress(address);
    }

    public static AccountAddress ofHexString(String hexString) {
        return new AccountAddress(Hex.decode(hexString));
    }

}
