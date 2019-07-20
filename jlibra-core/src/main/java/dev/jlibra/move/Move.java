package dev.jlibra.move;

import com.google.protobuf.ByteString;

import java.io.IOException;
import java.io.InputStream;

public class Move {

    public static final ByteString peerToPeerTransfer = peerToPeerTransfer();

    private static ByteString peerToPeerTransfer() {
        String programBinary = "/move/peer_to_peer_transfer.bin";
        InputStream p2pTransferStream = Move.class.getResourceAsStream(programBinary);
        try {
            return ByteString.readFrom(p2pTransferStream);
        } catch (IOException e) {
            throw new RuntimeException("Could not load program binary from " + programBinary);
        }
    }
}
