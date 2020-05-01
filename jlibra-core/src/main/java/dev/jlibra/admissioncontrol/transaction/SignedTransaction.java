package dev.jlibra.admissioncontrol.transaction;

import org.immutables.value.Value;

import dev.jlibra.serialization.lcs.LCS;

@LCS.Structure
@Value.Immutable
public interface SignedTransaction {

    @LCS.Field(0)
    public abstract Transaction getTransaction();

    @LCS.Field(1)
    public abstract Authenticator getAuthenticator();

}
