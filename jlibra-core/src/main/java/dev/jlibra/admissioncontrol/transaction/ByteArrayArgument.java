package dev.jlibra.admissioncontrol.transaction;

import dev.jlibra.serialization.lcs.LCS;

@LCS.ExternallyTaggedEnumeration(dev.jlibra.serialization.lcs.type.TransactionArgument.ByteArray)
public class ByteArrayArgument implements TransactionArgument {

    public static final int PREFIX = 2;

    private VariableLengthByteSequence bytes;

    public ByteArrayArgument(VariableLengthByteSequence bytes) {
        this.bytes = bytes;
    }

    @LCS.Field(0)
    public VariableLengthByteSequence getValue() {
        return bytes;
    }

}
