package dev.jlibra.example;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.jlibra.client.DiemClient;
import dev.jlibra.client.views.event.Event;

public class GetEventsByEventKeyExample {

    private static final Logger logger = LoggerFactory.getLogger(GetEventsByEventKeyExample.class);

    public static void main(String[] args) {
        String eventKey = "010000000000000025407727b0b497be4f31f8dd82843a9c";

        DiemClient client = DiemClient.builder()
                .withUrl("https://client.testnet.libra.org/v1/")
                .build();

        List<Event> events = client.getEvents(eventKey, 0, 10);

        events.forEach(e -> logger.info("Event: {}", e));
    }

}
