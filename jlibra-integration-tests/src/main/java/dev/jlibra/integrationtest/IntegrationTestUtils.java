package dev.jlibra.integrationtest;

import static java.lang.String.format;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.jlibra.AuthenticationKey;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

public class IntegrationTestUtils {

    private static final Logger logger = LoggerFactory.getLogger(IntegrationTestUtils.class);

    public static void mint(AuthenticationKey authenticationKey, long amountInMicroLibras) {
        HttpResponse<String> response = Unirest.post("http://faucet.testnet.libra.org")
                .queryString("amount", amountInMicroLibras)
                .queryString("auth_key", authenticationKey.toString())
                .asString();

        if (response.getStatus() != 200) {
            logger.error("Minting failed. Response {} {} {}", response.getStatus(), response.getStatusText(),
                    response.getBody());
            throw new IllegalStateException(
                    format("Error in minting %d Libra for authenticationKey %s", amountInMicroLibras,
                            authenticationKey));
        }
    }
}
