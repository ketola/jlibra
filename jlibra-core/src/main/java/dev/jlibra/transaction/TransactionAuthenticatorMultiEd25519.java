package dev.jlibra.transaction;

import org.immutables.value.Value;

import dev.jlibra.MultiSignaturePublicKey;
import dev.jlibra.serialization.bcs.BCS;

@Value.Immutable
public interface TransactionAuthenticatorMultiEd25519 extends Authenticator {

    @BCS.Field(0)
    MultiSignaturePublicKey getPublicKey();

    @BCS.Field(1)
    Signature getSignature();

}
