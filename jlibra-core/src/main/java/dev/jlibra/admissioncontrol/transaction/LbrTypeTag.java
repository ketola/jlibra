package dev.jlibra.admissioncontrol.transaction;

import java.util.ArrayList;

import dev.jlibra.AccountAddress;

public class LbrTypeTag {

    public static StructTag build() {
        return ImmutableStructTag.builder()
                .address(AccountAddress.fromHexString("00000000000000000000000000000000"))
                .name("LBR")
                .module("T")
                .typeParams(new ArrayList<>())
                .build();
    }

}
