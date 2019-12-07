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
        String toAddress = "1b2d1a2b57704043fa1f97fcc08e268f45d1c5b9f7b43c481941c103b99d8ca5";
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
