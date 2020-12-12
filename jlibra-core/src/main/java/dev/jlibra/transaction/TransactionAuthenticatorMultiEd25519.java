package dev.jlibra.transaction;

import org.immutables.value.Value;

import dev.jlibra.MultiSignaturePublicKey;
import dev.jlibra.serialization.lcs.DCS;

@Value.Immutable
public interface TransactionAuthenticatorMultiEd25519 extends Authenticator {

    @DCS.Field(0)
    MultiSignaturePublicKey getPublicKey();

    @DCS.Field(1)
    Signature getSignature();

}
