package uk.gov.hmcts.befta.util;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import java.util.ArrayList;
import java.util.List;

public class TestLogAppender extends AppenderBase<ILoggingEvent> {

    private final List<ILoggingEvent> logEvents = new ArrayList<>();

    @Override
    protected void append(ILoggingEvent eventObject) {
        logEvents.add(eventObject);
    }

    public List<ILoggingEvent> getLogEvents() {
        return new ArrayList<>(logEvents);
    }
}
