package uk.gov.hmcts.befta.util;

import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.hmcts.befta.exception.FunctionalTestException;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class HttpRequestRetryer {

    private static final Logger logger = LoggerFactory.getLogger(HttpRequestRetryer.class);
    private static final String ALL_HTTP_METHODS = "*";

    private HttpRequestRetryer() {
    }

    public static Retryer<Response> createRetryer(Retryable retryable, String method) {
        if (retryable.getNonRetryableHttpMethods().contains(ALL_HTTP_METHODS)
                || retryable.getNonRetryableHttpMethods().contains(method)) {
            logger.info("Applying no-retry policy...");
            return RetryerBuilder.<Response>newBuilder().build();
        }

        logger.info("Applying active retry policy... {}", retryable);
        return RetryerBuilder.<Response>newBuilder()
                .withRetryListener(retryable.getRetryListener())
                .retryIfException(e -> {
                    boolean isRetryableException = retryable.getRetryableExceptions().contains(e.getClass());
                    Throwable cause = e.getCause();
                    boolean isRetryableCause = cause != null && retryable.getRetryableExceptions()
                            .contains(cause.getClass());
                    return isRetryableException || isRetryableCause;
                })
                .retryIfResult(res -> retryable.getStatusCodes().contains(res.getStatusCode()))
                .retryIfResult(res -> {
                    for (String match : retryable.getMatch()) {
                        Pattern pattern = Pattern.compile(match);
                        Matcher matcher = pattern.matcher(res.asString());
                        if (matcher.find()) {
                            return true;
                        }
                    }
                    return false;
                })
                .withStopStrategy(StopStrategies.stopAfterAttempt(retryable.getMaxAttempts()))
                .withWaitStrategy(WaitStrategies.fixedWait(retryable.getDelay(), TimeUnit.MILLISECONDS))
                .build();
    }

    public static Response executeHttpRequestWithRetry(RequestSpecification theRequest, String method, String uri,
                                                       Retryer<Response> retryer) {
        try {
            Callable<Response> callable = () -> theRequest.request(method, uri);
            return retryer.call(callable);
        } catch (RetryException retryException) {
            throw new FunctionalTestException(
                    String.format("Retry Exception when calling %s", uri), retryException);
        } catch (ExecutionException executionException) {
            throw new FunctionalTestException(
                    String.format("Execution Exception when calling %s", uri), executionException);
        }
    }
}
