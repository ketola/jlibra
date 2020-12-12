package dev.jlibra.transaction;

import dev.jlibra.serialization.dcs.DCS.ExternallyTaggedEnumeration;

@ExternallyTaggedEnumeration(classes = { TransactionAuthenticatorEd25519.class,
        TransactionAuthenticatorMultiEd25519.class })
public interface Authenticator {

}
