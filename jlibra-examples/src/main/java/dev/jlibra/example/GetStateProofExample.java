package dev.jlibra.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.jlibra.client.LibraClient;
import dev.jlibra.client.views.StateProof;

public class GetStateProofExample {
    private static final Logger logger = LoggerFactory.getLogger(GetStateProofExample.class);

    public static void main(String[] args) {
        LibraClient client = LibraClient.builder()
                .withUrl("http://client.testnet.libra.org/")
                .build();

        StateProof stateProof = client.getStateProof(11252608);

        logger.info("State proof: {}", stateProof);
    }

}
