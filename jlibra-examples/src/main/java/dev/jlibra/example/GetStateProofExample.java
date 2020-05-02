package dev.jlibra.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.jlibra.client.LibraClient;
import dev.jlibra.client.views.StateProof;

public class GetStateProofExample {
    private static final Logger logger = LogManager.getLogger(GetStateProofExample.class);

    public static void main(String[] args) {
        LibraClient client = LibraClient.builder()
                .withUrl("http://client.testnet.libra.org/")
                .build();

        StateProof stateProof = client.getStateProof(11252608);

        logger.info(stateProof);
    }

}
