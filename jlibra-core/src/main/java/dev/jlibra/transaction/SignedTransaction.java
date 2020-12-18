package dev.jlibra.transaction;

import org.immutables.value.Value;

import dev.jlibra.serialization.bcs.BCS;

@BCS.Structure
@Value.Immutable
public interface SignedTransaction {

    @BCS.Field(0)
    Transaction getTransaction();

    @BCS.Field(1)
    Authenticator getAuthenticator();

}
