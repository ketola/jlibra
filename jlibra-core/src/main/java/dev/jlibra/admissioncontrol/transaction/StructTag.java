package dev.jlibra.admissioncontrol.transaction;

import java.util.List;

import org.immutables.value.Value;

import dev.jlibra.AccountAddress;
import dev.jlibra.serialization.lcs.LCS;

@Value.Immutable
@LCS.Structure(builderClass = ImmutableStructTag.class)
public interface StructTag extends TypeTag {

    @LCS.Field(value = 0, fixedLength = true)
    AccountAddress address();

    @LCS.Field(value = 1)
    String name();

    @LCS.Field(value = 2)
    String module();

    @LCS.Field(value = 3)
    List<String> typeParams();
}
