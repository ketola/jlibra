package dev.jlibra.transaction.argument;

import dev.jlibra.serialization.lcs.LCS.ExternallyTaggedEnumeration;
import dev.jlibra.transaction.NotImplemented;

@ExternallyTaggedEnumeration(classes = { U8Argument.class, U64Argument.class, NotImplemented.class,
        AccountAddressArgument.class,
        U8VectorArgument.class })
public interface TransactionArgument {
}
