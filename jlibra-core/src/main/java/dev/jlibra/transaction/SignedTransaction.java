package dev.jlibra.transaction;

import org.immutables.value.Value;

import dev.jlibra.serialization.lcs.DCS;

@DCS.Structure
@Value.Immutable
public interface SignedTransaction {

    @DCS.Field(0)
    Transaction getTransaction();

    @DCS.Field(1)
    Authenticator getAuthenticator();

}
