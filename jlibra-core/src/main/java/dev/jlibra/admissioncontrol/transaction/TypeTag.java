package dev.jlibra.admissioncontrol.transaction;

import dev.jlibra.serialization.lcs.LCS;

@LCS.ExternallyTaggedEnumeration(value = 0, types = { U64Argument.class, U64Argument.class,
        U64Argument.class, U64Argument.class, U64Argument.class, U64Argument.class, StructTag.class })
public interface TypeTag {

}
