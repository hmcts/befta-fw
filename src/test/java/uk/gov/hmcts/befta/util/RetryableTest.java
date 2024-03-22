package uk.gov.hmcts.befta.util;

import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLException;
import java.net.SocketException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RetryableTest {

    @Test
    void createFromConfigFile_WithValidConfigFile_ShouldCreateRetryableInstance() {
        Retryable retryable = Retryable.createFromConfiguration("valid-config.properties");

        assertNotNull(retryable);
        assertEquals(3, retryable.getMaxAttempts());
        assertEquals(ImmutableSet.of(200, 404, 500), retryable.getStatusCodes());
        assertEquals(500, retryable.getDelay());
        assertEquals(ImmutableSet.of(javax.net.ssl.SSLException.class, java.net.SocketException.class),
                retryable.getRetryableExceptions());
        assertEquals(ImmutableSet.of("POST", "GET"),
                retryable.getNonRetryableHttpMethods());
    }

    @Test
    void createFromConfigFile_WithInvalidConfigFile_ShouldReturnNoRetryable() {
        Retryable retryable = Retryable.createFromConfiguration("invalid-config.properties");

        assertNotNull(retryable);
        assertEquals(Retryable.DEFAULT_RETRYABLE.getDelay(), retryable.getDelay());
        assertEquals(Retryable.DEFAULT_RETRYABLE.getMaxAttempts(), retryable.getMaxAttempts());
        assertTrue(retryable.getStatusCodes().isEmpty());
    }

    @Test
    void createFromConfigFile_WithMissingConfigFile_ShouldReturnNoRetryable() {
        Retryable retryable = Retryable.createFromConfiguration("nonexistent-config.properties");

        assertNotNull(retryable);
        assertEquals(Retryable.DEFAULT_RETRYABLE, retryable);
    }

    @Test
    void createFromConfigFile_WithNullConfigFile_ShouldReturnNoRetryable() {
        Retryable retryable = Retryable.createFromConfiguration(null);

        assertNotNull(retryable);
        assertEquals(Retryable.DEFAULT_RETRYABLE, retryable);
    }

    @Test
    void createFromConfigFile_WithEmptyConfigFile_ShouldReturnNoRetryable() {
        Retryable retryable = Retryable.createFromConfiguration("");

        assertNotNull(retryable);
        assertEquals(Retryable.DEFAULT_RETRYABLE, retryable);
    }

    @Test
    void createFromConfigFile_WithDefaultConfigFile_ShouldCreateRetryableInstance() {
        Retryable retryable = Retryable.RETRYABLE_FROM_CONFIG;

        assertNotNull(retryable);
        assertEquals(1, retryable.getMaxAttempts());
        assertEquals(ImmutableSet.of(500, 502, 503, 504), retryable.getStatusCodes());
        assertEquals(1000, retryable.getDelay());
    }

    @Test
    void createFromConfigFile_WithRetryableExceptions_ShouldCreateRetryableInstanceWithExceptions() {
        Retryable retryable = Retryable.createFromConfiguration("retryable-exceptions-config.properties");

        assertNotNull(retryable);
        assertEquals(1, retryable.getMaxAttempts());
        assertEquals(ImmutableSet.of(), retryable.getStatusCodes());
        assertEquals(0, retryable.getDelay());

        Set<Class<? extends Exception>> expectedExceptions = retryable.getRetryableExceptions();
        assertTrue(expectedExceptions.contains(SocketException.class));
        assertTrue(expectedExceptions.contains(SSLException.class));
    }
}