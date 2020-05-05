package dev.jlibra.example;

import static java.lang.String.format;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

/**
 * Calls the faucet service http endpoint with parameters authentication key and
 * amount (in microLibras)
 * 
 * If the request is successful, the service returns an id number and the minted
 * amount should be available for the account.
 * 
 * This works only for the libra testnet, to mint in another (t. ex. local)
 * environment, you would need to create a mint transaction which requires
 * special permissions.
 * 
 */
public class MintExample {

    private static final Logger logger = LoggerFactory.getLogger(MintExample.class);

    public static void main(String[] args) {
        String authenticationKey = "57cc5b453426e3d8e212c7d25e128014ee25121cec7f67d0eb8b7ecbff27e742";
        long amountInMicroLibras = 10L * 1_000_000L;

        HttpResponse<String> response = Unirest.post("http://faucet.testnet.libra.org")
                .queryString("amount", amountInMicroLibras)
                .queryString("auth_key", authenticationKey)
                .asString();

        if (response.getStatus() != 200) {
            throw new IllegalStateException(
                    format("Error in minting %d Libra for authentication key %s. Response: %d, message: %s",
                            amountInMicroLibras,
                            authenticationKey, response.getStatus(), response.getStatusText()));

        }
        logger.info(response.getBody());

    }
}
