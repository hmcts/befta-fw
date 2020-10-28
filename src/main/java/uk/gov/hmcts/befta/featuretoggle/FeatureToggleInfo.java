package uk.gov.hmcts.befta.featuretoggle;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class FeatureToggleInfo {

    private Map<String, Boolean> statuses = new ConcurrentHashMap<>();

    public FeatureToggleInfo() {
    }

    public void add(String flag, Boolean enabled) {
        statuses.put(flag, enabled);
    }

    public boolean isAnyEnabled() {
        return statuses.values().contains(Boolean.TRUE);
    }

    public boolean isAllEnabled() {
        return !isAnyDisabled();
    }

    public boolean isAnyDisabled() {
        return statuses.values().contains(Boolean.FALSE);
    }

    public boolean isAllDisabled() {
        return !isAnyEnabled();
    }

    public List<String> getDisabledFeatureFlags() {
        return statuses.entrySet().stream().filter(e -> !e.getValue()).map(e -> e.getKey())
                .collect(Collectors.toList());
    }

    public List<String> getEnabledFeatureFlags() {
        return statuses.entrySet().stream().filter(e -> e.getValue()).map(e -> e.getKey()).collect(Collectors.toList());
    }

}
