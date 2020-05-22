package dev.jlibra.transaction;

import org.immutables.value.Value;

import dev.jlibra.MultiSignaturePublicKey;
import dev.jlibra.serialization.lcs.LCS;

@Value.Immutable
public interface TransactionAuthenticatorMultiEd25519 extends Authenticator {

    @LCS.Field(0)
    MultiSignaturePublicKey getPublicKey();

    @LCS.Field(1)
    Signature getSignature();

}
