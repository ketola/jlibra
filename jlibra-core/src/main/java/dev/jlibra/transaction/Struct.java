package dev.jlibra.transaction;

import java.util.ArrayList;
import java.util.List;

import org.immutables.value.Value;

import dev.jlibra.AccountAddress;
import dev.jlibra.serialization.dcs.DCS;

@Value.Immutable
public interface Struct extends TypeTag {

    @DCS.Field(value = 0, fixedLength = true)
    public AccountAddress address();

    @DCS.Field(value = 1)
    String module();

    @DCS.Field(value = 2)
    String name();

    @DCS.Field(value = 3)
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
