package dev.jlibra.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

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

    public static void main(String[] args) throws IOException {
        String toAddress = "045d3e63dba85f759d66f9bed4a0e4c262d17f9713f25e846fdae63891837a98";
        long amountInMicroLibras = 10L * 1_000_000L;

        URL faucet = new URL(
                String.format("http://faucet.testnet.libra.org?amount=%d&address=%s", amountInMicroLibras, toAddress));

        URLConnection uc = faucet.openConnection();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        uc.getInputStream()));

        String inputLine;
        while ((inputLine = in.readLine()) != null)
            System.out.println(inputLine);
        in.close();

        System.out.println("Done.");
    }

}
