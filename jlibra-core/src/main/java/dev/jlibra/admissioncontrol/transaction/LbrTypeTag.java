package dev.jlibra.admissioncontrol.transaction;

import java.util.ArrayList;
import java.util.List;

import dev.jlibra.AccountAddress;
import dev.jlibra.serialization.lcs.LCS;

@LCS.ExternallyTaggedEnumeration(dev.jlibra.serialization.lcs.type.TypeTag.Struct)
public class LbrTypeTag {

    @LCS.Field(value = 0, fixedLength = true)
    public AccountAddress getAddress() {
        return AccountAddress.fromHexString("00000000000000000000000000000000");
    }

    @LCS.Field(value = 1)
    public String getName() {
        return "LBR";
    }

    @LCS.Field(value = 2)
    public String getModule() {
        return "T";
    }

    @LCS.Field(value = 3)
    public List<?> getTypeParams() {
        return new ArrayList<>();
    }
}
