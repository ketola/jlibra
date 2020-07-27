package dev.jlibra.example;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.jlibra.client.LibraClient;
import dev.jlibra.client.views.event.Event;

public class GetEventsByEventKeyExample {

    private static final Logger logger = LoggerFactory.getLogger(GetEventsByEventKeyExample.class);

    public static void main(String[] args) {
        String eventKey = "01000000000000007d7aaaee9177d12b60cdf79dc894aeb9";

        LibraClient client = LibraClient.builder()
                .withUrl("https://client.testnet.libra.org/v1/")
                .build();

        List<Event> events = client.getEvents(eventKey, 0, 10);

        events.forEach(e -> logger.info("Event: {}", e));
    }

}
