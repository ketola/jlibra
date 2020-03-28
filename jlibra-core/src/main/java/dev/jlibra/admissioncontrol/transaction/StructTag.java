package dev.jlibra.admissioncontrol.transaction;

import java.util.ArrayList;
import java.util.List;

import dev.jlibra.AccountAddress;
import dev.jlibra.serialization.lcs.LCS;

@LCS.ExternallyTaggedEnumeration(dev.jlibra.serialization.lcs.type.TypeTag.Struct)
public class StructTag {

    @LCS.Field(value = 0, fixedLength = true)
    public AccountAddress getAddress() {
        return AccountAddress.fromHexString("00000000000000000000000000000000");
    }

    @LCS.Field(value = 1)
    public String getModule() {
        return "T";
    }

    @LCS.Field(value = 2)
    public String getName() {
        return "LBR";
    }

    @LCS.Field(value = 3)
    public List<?> getTypeParams() {
        return new ArrayList<>();
    }

    public static final String STRUCT_TAG_ADDRESS = "LBR";
    public static final String STRUCT_TAG_ACCOUNT_ADDRESS = "0000000000000000000000000000000000000000000000000000000000000000";
    public static final byte RESOURCE_TAG = 1;

    /**
     * public static void ss() { ByteArray serializedStructTag =
     * Serializer.builder()
     * .appendFixedLength(ByteArray.from(STRUCT_TAG_ACCOUNT_ADDRESS))
     * .appendString(STRUCT_TAG_ADDRESS) .appendString(STRUCT_TAG_MODULE)
     * .appendInt(STRUCT_TAG_TYPE_PARAMS_LENGTH) .toByteArray();
     * 
     * }
     */
}
