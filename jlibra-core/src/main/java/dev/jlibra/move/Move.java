package dev.jlibra.move;

import com.google.protobuf.ByteString;

import org.bouncycastle.util.encoders.Hex;

public class Move {

    private static final String peerToPeerTransferBinary =
            "4c49425241564d0a010007014a00000004000000034e000000060000000c5" +
            "4000000060000000d5a000000060000000560000000290000000489000000" +
            "2000000007a90000000e00000000000001000200010300020002040200030" +
            "003020402063c53454c463e0c4c696272614163636f756e74046d61696e0f" +
            "7061795f66726f6d5f73656e6465720000000000000000000000000000000" +
            "0000000000000000000000000000000000001020104000c000c0111010002";

    public static final ByteString peerToPeerTransfer = ByteString.copyFrom(Hex.decode(peerToPeerTransferBinary));
}
