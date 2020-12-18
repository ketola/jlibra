package dev.jlibra.transaction;

import org.immutables.value.Value;

import dev.jlibra.PublicKey;
import dev.jlibra.serialization.bcs.BCS;

@Value.Immutable
public interface TransactionAuthenticatorEd25519 extends Authenticator {

    @BCS.Field(0)
    PublicKey getPublicKey();

    @BCS.Field(1)
    Signature getSignature();

}
