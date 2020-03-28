package dev.jlibra.example;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.jlibra.client.LibraJsonRpcClient;
import dev.jlibra.client.LibraJsonRpcClientBuilder;
import dev.jlibra.client.views.StateProof;

public class GetStateProofJsonRpcExample {
    private static final Logger logger = LogManager.getLogger(GetStateProofJsonRpcExample.class);

    public static void main(String[] args) {
        LibraJsonRpcClient client = LibraJsonRpcClientBuilder.builder()
                .withUrl("http://client.testnet.libra.org/")
                .build();

        StateProof stateProof = client.getStateProof(11252608);

        logger.info(stateProof);
    }

}
