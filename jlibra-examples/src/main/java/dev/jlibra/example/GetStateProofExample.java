package dev.jlibra.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.jlibra.client.DiemClient;
import dev.jlibra.client.views.StateProof;

public class GetStateProofExample {
    private static final Logger logger = LoggerFactory.getLogger(GetStateProofExample.class);

    public static void main(String[] args) {
        DiemClient client = DiemClient.builder()
                .withUrl("http://localhost:8080")
                .build();

        StateProof stateProof = client.getStateProof(324);

        logger.info("State proof: {}", stateProof);
    }

}
