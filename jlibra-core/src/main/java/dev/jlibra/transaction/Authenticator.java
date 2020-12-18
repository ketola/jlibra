package dev.jlibra.transaction;

import dev.jlibra.serialization.bcs.BCS.ExternallyTaggedEnumeration;

@ExternallyTaggedEnumeration(classes = { TransactionAuthenticatorEd25519.class,
        TransactionAuthenticatorMultiEd25519.class })
public interface Authenticator {

}
