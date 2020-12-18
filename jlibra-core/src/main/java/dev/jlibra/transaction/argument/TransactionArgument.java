package dev.jlibra.transaction.argument;

import dev.jlibra.serialization.bcs.BCS.ExternallyTaggedEnumeration;
import dev.jlibra.transaction.NotImplemented;

@ExternallyTaggedEnumeration(classes = { U8Argument.class, U64Argument.class, NotImplemented.class,
        AccountAddressArgument.class, U8VectorArgument.class, BoolArgument.class })
public interface TransactionArgument {
}
