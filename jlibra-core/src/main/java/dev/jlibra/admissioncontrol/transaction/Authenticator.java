package dev.jlibra.admissioncontrol.transaction;

import dev.jlibra.serialization.lcs.LCS.ExternallyTaggedEnumeration;

@ExternallyTaggedEnumeration(classes = { TransactionAuthenticatorEd25519.class,
        TransactionAuthenticatorMultiEd25519.class })
public interface Authenticator {

}
