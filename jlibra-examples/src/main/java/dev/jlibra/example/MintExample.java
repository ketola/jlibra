package dev.jlibra.example;

import static java.lang.String.format;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private static final Logger logger = LogManager.getLogger(MintExample.class);

    public static void main(String[] args) {
        System.out.println("dsa");
        String authenticationKey = "97784bdb5fde351a3fa8e400265989a34e03aec69589026b4a095c9cd2e53ca6";
        long amountInMicroLibras = 10L * 1_000_000L;

        for (int i = 0; i <= 1; i++) {
            HttpResponse<String> response = Unirest.post("http://faucet.testnet.libra.org")
                    .queryString("amount", amountInMicroLibras)
                    .queryString("auth_key", authenticationKey)
                    .asString();

            if (response.getStatus() != 200) {
                throw new IllegalStateException(
                        format("Error in minting %d Libra for authentication key %s", amountInMicroLibras,
                                authenticationKey));

            }
            logger.info(i + " " + response.getBody());
        }

    }
}
