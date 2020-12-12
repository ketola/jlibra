package dev.jlibra.poller;

import static java.lang.Boolean.FALSE;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.jlibra.LibraRuntimeException;
import net.jodah.failsafe.Failsafe;
import net.jodah.failsafe.RetryPolicy;

public class Wait {
    private static final Logger logger = LoggerFactory.getLogger(Wait.class);

    public static void until(WaitCondition waitCondition) {
        RetryPolicy<Boolean> retryPolicy = new RetryPolicy<Boolean>()
                .onFailedAttempt(
                        e -> logger.info("Condition was not met. Retrying..", e.getLastFailure()))
                .withDelay(Duration.ofMillis(1000))
                .withMaxRetries(10)
                .withMaxDuration(Duration.ofSeconds(15))
                .handleResult(Boolean.FALSE);

        Boolean result = Failsafe.with(retryPolicy).get((e) -> waitCondition.isFulfilled());

        if (result == FALSE) {
            throw new LibraRuntimeException("Wait condition was not fulfilled");
        }
    }

}
