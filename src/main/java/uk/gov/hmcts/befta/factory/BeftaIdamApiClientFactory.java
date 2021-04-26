/**
 * 
 */
package uk.gov.hmcts.befta.factory;

import feign.Feign;
import feign.Logger;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import uk.gov.hmcts.befta.BeftaMain;
import uk.gov.hmcts.befta.auth.AuthApi;
import uk.gov.hmcts.befta.util.BeftaUtils;

/**
 * @author korneleehenry
 *
 */
public class BeftaIdamApiClientFactory {

    private BeftaIdamApiClientFactory() {
    }

    public static AuthApi createAuthorizationClient() {

        Feign.Builder feignBuilder = Feign.builder()
                .encoder(new JacksonEncoder())
                .decoder(new JacksonDecoder());

        if (BeftaMain.getConfig().isHttpLoggingEnabled()) {
            feignBuilder
                    .logger(new BeftaHttpLogger())
                    .logLevel(Logger.Level.FULL);
        }

        return feignBuilder.target(AuthApi.class, BeftaMain.getConfig().getIdamURL());
    }

    private static class BeftaHttpLogger extends Logger {
        @Override
        protected void log(String configKey, String format, Object... args) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(configKey);
            stringBuilder.append(" ");
            stringBuilder.append(String.format(format, args));

            BeftaUtils.defaultLog(stringBuilder.toString());
        }
    }

}
