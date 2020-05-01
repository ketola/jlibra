package dev.jlibra.example;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.jlibra.client.LibraClient;
import dev.jlibra.client.views.Event;

public class GetEventsByEventKeyExample {
    private static final Logger logger = LogManager.getLogger(GetEventsByEventKeyExample.class);

    public static void main(String[] args) {
        String eventKey = "0000000000000000b3f7e8e38f8c8393f281a2f0792a2849";

        LibraClient client = LibraClient.builder()
                .withUrl("http://client.testnet.libra.org/")
                .build();

        List<Event> events = client.getEvents(eventKey, 0, 10);

        events.forEach(logger::info);
    }

}
