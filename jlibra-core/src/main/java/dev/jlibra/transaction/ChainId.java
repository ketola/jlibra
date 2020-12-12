package dev.jlibra.transaction;

import org.immutables.value.Value;

import dev.jlibra.serialization.dcs.DCS;

@Value.Immutable
@DCS.Structure
public interface ChainId {

    public static final ChainId MAINNET = fromInt(1);
    public static final ChainId TESTNET = fromInt(2);
    public static final ChainId DEVNET = fromInt(3);
    public static final ChainId TESTING = fromInt(4);

    @DCS.Field(0)
    byte value();

    public static ChainId fromInt(int value) {
        return ImmutableChainId.builder()
                .value(Byte.valueOf((byte) value))
                .build();
    }

}
