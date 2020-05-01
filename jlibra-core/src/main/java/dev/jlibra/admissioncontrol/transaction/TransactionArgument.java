package dev.jlibra.admissioncontrol.transaction;

import dev.jlibra.serialization.lcs.LCS.ExternallyTaggedEnumeration;

@ExternallyTaggedEnumeration(classes = { U64Argument.class, AccountAddressArgument.class, ByteArrayArgument.class })
public interface TransactionArgument {
}
