package uk.gov.hmcts.befta.featuretoggle;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ScenarioFeatureToggleInfo {
    //rename to actualToggleStatus
    private Map<String, Boolean> actualStatuses = new ConcurrentHashMap<>();
    private Map<String, String> expectedStatuses = new ConcurrentHashMap<>();

    //New Map of expected status

    public ScenarioFeatureToggleInfo() {
    }

    public void add(String flag, Boolean enabled) {
        actualStatuses.put(flag, enabled);
    }

    public boolean isAnyEnabled() {
        return actualStatuses.values().contains(Boolean.TRUE);
    }

    public boolean isAllEnabled() {
        return !isAnyDisabled();
    }

    public boolean isAnyDisabled() {
        return actualStatuses.values().contains(Boolean.FALSE);
    }

    public boolean isAllDisabled() {
        return !isAnyEnabled();
    }

    public List<String> getDisabledFeatureFlags() {
        return actualStatuses.entrySet().stream().filter(e -> !e.getValue()).map(e -> e.getKey())
                .collect(Collectors.toList());
    }

    public List<String> getEnabledFeatureFlags() {
        return actualStatuses.entrySet().stream().filter(e -> e.getValue()).map(e -> e.getKey()).collect(Collectors.toList());
    }

    public boolean matchesExpectations() {
        //match expected and actual statuses
    }

}
