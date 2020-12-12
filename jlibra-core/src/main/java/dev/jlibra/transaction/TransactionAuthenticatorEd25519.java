package dev.jlibra.transaction;

import org.immutables.value.Value;

import dev.jlibra.PublicKey;
import dev.jlibra.serialization.lcs.DCS;

@Value.Immutable
public interface TransactionAuthenticatorEd25519 extends Authenticator {

    @DCS.Field(0)
    PublicKey getPublicKey();

    @DCS.Field(1)
    Signature getSignature();

}
