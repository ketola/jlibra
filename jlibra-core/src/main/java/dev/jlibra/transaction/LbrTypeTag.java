package dev.jlibra.transaction;

import java.util.ArrayList;
import java.util.List;

import dev.jlibra.AccountAddress;
import dev.jlibra.serialization.lcs.LCS;

public class LbrTypeTag implements TypeTag {

    @LCS.Field(value = 0, fixedLength = true)
    public AccountAddress getAddress() {
        return AccountAddress.fromHexString("00000000000000000000000000000001");
    }

    @LCS.Field(value = 1)
    public String getModule() {
        return "LBR";
    }

    @LCS.Field(value = 2)
    public String getName() {
        return "LBR";
    }

    @LCS.Field(value = 3)
    public List<?> getTypeParams() {
        return new ArrayList<>();
    }
}
