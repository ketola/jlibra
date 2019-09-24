package dev.jlibra.example;

import java.io.IOException;

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

    public static void main(String[] args) throws IOException {
        String toAddress = "6674633c78e2e00c69fd6e027aa6d1db2abc2a6c80d78a3e129eaf33dd49ce1c";
        long amountInMicroLibras = 10L * 1_000_000L;

        HttpResponse<String> response = Unirest.post("http://faucet.testnet.libra.org")
                .queryString("amount", amountInMicroLibras)
                .queryString("address", toAddress)
                .asString();

        if (response.getStatus() != 200) {
            throw new IllegalStateException(
                    String.format("Error in minting %d Libra for address %s", amountInMicroLibras, toAddress));
        }

        logger.info(response.getBody());
    }

}
