package dev.jlibra.move;

import java.io.InputStream;

public class Move {

    public static InputStream peerToPeerTransfer() {
        return Move.class.getResourceAsStream("/move/peer_to_peer_transfer.bin");
    }

}
