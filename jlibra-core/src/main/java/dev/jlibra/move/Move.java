package dev.jlibra.move;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Move {

    public static byte[] peerToPeerTransferAsBytes() {
        try {
            String json = readJson();
            String[] bytesAsString = json.substring(json.indexOf('[') + 1, json.indexOf(']')).split(",");
            byte[] bytes = new byte[bytesAsString.length];
            for (int idx = 0; idx < bytesAsString.length; idx++) {
                bytes[idx] = (byte)Integer.parseInt(bytesAsString[idx]);
            }
            return bytes;
        } catch (Exception ex) {
            throw new RuntimeException("Error reading p2p transaction script.", ex);
        }
    }

    private static String readJson() throws IOException {
        ClassLoader classLoader = Move.class.getClassLoader();
        File jsonFile = new File(classLoader.getResource("move/peer_to_peer_transfer.bin.json").getFile());
        return new String(Files.readAllBytes(Paths.get(jsonFile.getPath())), Charset.forName("UTF-8"));
    }
}