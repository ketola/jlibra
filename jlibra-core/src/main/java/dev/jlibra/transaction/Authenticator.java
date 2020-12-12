package dev.jlibra.transaction;

import dev.jlibra.serialization.lcs.DCS.ExternallyTaggedEnumeration;

@ExternallyTaggedEnumeration(classes = { TransactionAuthenticatorEd25519.class,
        TransactionAuthenticatorMultiEd25519.class })
public interface Authenticator {

}
