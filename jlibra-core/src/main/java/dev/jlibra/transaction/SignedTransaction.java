package dev.jlibra.transaction;

import org.immutables.value.Value;

import dev.jlibra.serialization.lcs.LCS;

@LCS.Structure
@Value.Immutable
public interface SignedTransaction {

    @LCS.Field(0)
    Transaction getTransaction();

    @LCS.Field(1)
    Authenticator getAuthenticator();

}
