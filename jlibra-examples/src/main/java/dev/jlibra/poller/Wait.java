package dev.jlibra.poller;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;

public class Wait {
    private static final Logger logger = LogManager.getLogger(Wait.class);

    public static void until(WaitCondition waitCondition) {
        RetryPolicy<Boolean> retryPolicy = new RetryPolicy<Boolean>()
                .onFailedAttempt(
                        e -> logger.info("Condition was not met. Retrying..", e.getLastFailure()))
                .withDelay(Duration.ofMillis(1000))
                .withMaxRetries(5)
                .withMaxDuration(Duration.ofSeconds(10))
                .handleResult(Boolean.FALSE);

        Failsafe.with(retryPolicy).get(() -> waitCondition.isFulfilled());
    }

}
