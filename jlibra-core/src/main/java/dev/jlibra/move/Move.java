package dev.jlibra.move;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Move {

    public static byte[] peerToPeerTransferAsBytes() {
        try {
            InputStream jsonCode = Move.class.getResourceAsStream("/move/peer_to_peer_transfer.bin.json");
            String json = readFullyAsString(jsonCode, "UTF-8");
            String[] bytesAsString = json.substring(json.indexOf("[")+1, json.indexOf("]")).split(",");
            byte[] bytes = new byte[bytesAsString.length];
            int idx = 0;
            for (String byteAsString : bytesAsString) {
                bytes[idx++] = (byte)(Integer.valueOf(byteAsString).byteValue() & 0xFF);
            }
            return bytes;
        } catch (Exception ex) {
            throw new RuntimeException("Error reading p2p transaction script.", ex);
        }
    }

    private static String readFullyAsString(InputStream inputStream, String encoding) throws IOException {
        return readFully(inputStream).toString(encoding);
    }

    private static ByteArrayOutputStream readFully(InputStream inputStream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        return baos;
    }
}