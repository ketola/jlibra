package dev.jlibra.transaction;

import java.util.ArrayList;
import java.util.List;

import org.immutables.value.Value;

import dev.jlibra.AccountAddress;
import dev.jlibra.serialization.bcs.BCS;

@Value.Immutable
public interface Struct extends TypeTag {

    @BCS.Field(value = 0, fixedLength = true)
    public AccountAddress address();

    @BCS.Field(value = 1)
    String module();

    @BCS.Field(value = 2)
    String name();

    @BCS.Field(value = 3)
    List<?> getTypeParams();

    public static Struct typeTagForCurrency(String currencyCode) {
        return ImmutableStruct.builder()
                .address(AccountAddress.fromHexString("00000000000000000000000000000001"))
                .module(currencyCode)
                .name(currencyCode)
                .typeParams(new ArrayList<>())
                .build();
    }
}
