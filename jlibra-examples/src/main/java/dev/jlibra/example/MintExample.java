package dev.jlibra.example;

import static java.lang.String.format;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

/**
 * Calls the faucet service http endpoint with parameters address and amount (in
 * microLibras)
 * 
 * If the request is successful, the service returns an id number and the minted
 * amount should be available for the address.
 * 
 * This works only for the libra testnet, to mint in another (t. ex. local)
 * environment, you would need to create a mint transaction which requires
 * special permissions.
 * 
 */
public class MintExample {

    private static final Logger logger = LogManager.getLogger(MintExample.class);

    public static void main(String[] args) {
        String toAddress = "1b5fbc93e89a775db9203a857181c88a9e6e2c96ed385c3f1bd4a1b1dd99f7b2";
        long amountInMicroLibras = 10L * 1_000_000L;

        HttpResponse<String> response = Unirest.post("http://faucet.testnet.libra.org")
                .queryString("amount", amountInMicroLibras)
                .queryString("address", toAddress)
                .asString();

        if (response.getStatus() != 200) {
            throw new IllegalStateException(
                    format("Error in minting %d Libra for address %s", amountInMicroLibras, toAddress));
        }

        logger.info(response.getBody());
    }
}
