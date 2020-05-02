package dev.jlibra.example.util;

import static java.lang.String.format;

import dev.jlibra.AuthenticationKey;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

public class ExampleUtils {

    public static void mint(AuthenticationKey authenticationKey, long amountInMicroLibras) {
        HttpResponse<String> response = Unirest.post("http://faucet.testnet.libra.org")
                .queryString("amount", amountInMicroLibras)
                .queryString("auth_key", authenticationKey.toString())
                .asString();

        if (response.getStatus() != 200) {
            throw new IllegalStateException(
                    format("Error in minting %d Libra for authenticationKey %s", amountInMicroLibras,
                            authenticationKey));
        }
    }
}
