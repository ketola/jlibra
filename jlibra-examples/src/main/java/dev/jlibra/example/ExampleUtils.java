package dev.jlibra.example;

import static java.lang.String.format;

import dev.jlibra.serialization.ByteSequence;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

public class ExampleUtils {

    public static void mint(ByteSequence address, long amountInMicroLibras) {
        HttpResponse<String> response = Unirest.post("http://faucet.testnet.libra.org")
                .queryString("amount", amountInMicroLibras)
                .queryString("address", address.toString())
                .asString();

        if (response.getStatus() != 200) {
            throw new IllegalStateException(
                    format("Error in minting %d Libra for address %s", amountInMicroLibras, address));
        }
    }
}
