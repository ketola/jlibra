package dev.jlibra.move;

import org.apache.commons.io.IOUtils;

public class Move {

    public static byte[] peerToPeerTransfer() {
        try {
            return IOUtils.toByteArray(Move.class.getResourceAsStream("/move/peer_to_peer_transfer.bin"));
        } catch (Exception e) {
            throw new RuntimeException("Reading the transfer script file failed", e);
        }
    }

}
