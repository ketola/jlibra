package dev.jlibra.transaction;

import org.immutables.value.Value;

import dev.jlibra.PublicKey;
import dev.jlibra.serialization.lcs.LCS;

@Value.Immutable
public interface TransactionAuthenticatorEd25519 extends Authenticator {

    @LCS.Field(0)
    PublicKey getPublicKey();

    @LCS.Field(1)
    Signature getSignature();

}
