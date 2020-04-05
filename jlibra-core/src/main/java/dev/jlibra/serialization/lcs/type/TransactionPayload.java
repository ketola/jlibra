package dev.jlibra.serialization.lcs.type;

import dev.jlibra.admissioncontrol.transaction.ImmutableScript;

public class TransactionPayload implements LibraEnum {
    public static final int Program = 0;
    public static final int WriteSet = 1;
    public static final int Script = 2;
    public static final int Module = 3;

    @Override
    public Class<?> get(int identifier) {
        return ImmutableScript.class;
    }
}
