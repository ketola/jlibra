package dev.jlibra.example;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.jlibra.client.LibraClient;
import dev.jlibra.client.views.Event;

public class GetEventsByEventKeyExample {

    private static final Logger logger = LoggerFactory.getLogger(GetEventsByEventKeyExample.class);

    public static void main(String[] args) {
        String eventKey = "01000000000000002ab3189806488e73014e2e429e45c143";

        LibraClient client = LibraClient.builder()
                .withUrl("http://client.testnet.libra.org/")
                .build();

        List<Event> events = client.getEvents(eventKey, 0, 10);

        events.forEach(e -> logger.info("Event: {}", e));
    }

}
