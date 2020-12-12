package dev.jlibra.transaction;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;

import org.immutables.value.Value;

import dev.jlibra.AccountAddress;
import dev.jlibra.DiemRuntimeException;
import dev.jlibra.serialization.ByteArray;
import dev.jlibra.serialization.Serializer;

@Value.Immutable
public interface DualAttestation {

    public static final ByteArray DOMAIN_SEPARATOR = ByteArray
            .from("@@$$LIBRA_ATTEST$$@@".getBytes(StandardCharsets.UTF_8));

    ByteArray metadata();

    AccountAddress payerAddress();

    Long amount();

    default ByteArray sign(PrivateKey privateKey) {
        byte[] metadataBytes = metadata().toArray();
        byte[] payerAddressBytes = Serializer.builder().appendFixedLength(payerAddress()).toByteArray().toArray();
        byte[] amountBytes = Serializer.builder().appendLong(amount()).toByteArray().toArray();
        byte[] domainSepatorBytes = DOMAIN_SEPARATOR.toArray();
        byte[] messageBytes = new byte[metadataBytes.length + payerAddressBytes.length + amountBytes.length
                + domainSepatorBytes.length];

        System.arraycopy(metadataBytes, 0, messageBytes, 0, metadataBytes.length);
        System.arraycopy(payerAddressBytes, 0, messageBytes, metadataBytes.length, payerAddressBytes.length);
        System.arraycopy(amountBytes, 0, messageBytes, metadataBytes.length + payerAddressBytes.length,
                amountBytes.length);
        System.arraycopy(domainSepatorBytes, 0, messageBytes,
                metadataBytes.length + payerAddressBytes.length + amountBytes.length, domainSepatorBytes.length);

        byte[] signature;

        try {
            java.security.Signature sgr = java.security.Signature.getInstance("Ed25519", "BC");
            sgr.initSign(privateKey);
            sgr.update(messageBytes);
            signature = sgr.sign();
        } catch (Exception e) {
            throw new DiemRuntimeException("Signing failed", e);
        }

        return ByteArray.from(signature);
    }

}
