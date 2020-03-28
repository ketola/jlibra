package dev.jlibra.admissioncontrol.transaction;

import org.immutables.value.Value;

import dev.jlibra.serialization.ByteSequence;
import dev.jlibra.serialization.lcs.LCS;

@LCS.ExternallyTaggedEnumeration(0)
@Value.Immutable
public interface TransactionAuthenticator {

    @LCS.Field(0)
    public abstract ByteSequence getPublicKey();

    @LCS.Field(1)
    public abstract Signature getSignature();

}
