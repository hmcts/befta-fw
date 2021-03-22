package uk.gov.hmcts.befta.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import uk.gov.hmcts.befta.BeftaMain;
import uk.gov.hmcts.befta.util.ElementIdFinder;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

@Data
public class CollectionVerificationConfig {

    public static final String OPERATOR_FIELD_NAME = "__operator__";
    public static final String ORDERING_FIELD_NAME = "__ordering__";
    public static final String ELEMENT_ID_FIELD_NAME = "__elementId__";

    public static final CollectionVerificationConfig DEFAULT = new CollectionVerificationConfig();

    @JsonProperty(OPERATOR_FIELD_NAME)
    private Operator operator;

    @JsonProperty(ORDERING_FIELD_NAME)
    private Ordering ordering;

    @JsonProperty(ELEMENT_ID_FIELD_NAME)
    private String elementId;

    public static enum Operator {
        EQUIVALENT, SUPERSET, SUBSET;
        public static Operator of(String key) {
            return key == null ? null : valueOf(key.toUpperCase());
        }
    }

    public static enum Ordering {
        ORDERED, UNORDERED;
        public static Ordering of(String key) {
            return key == null ? null : valueOf(key.toUpperCase());
        }
    }

    public CollectionVerificationConfig() {
        this(Operator.EQUIVALENT, Ordering.ORDERED, "id");
    }

    public CollectionVerificationConfig(Operator operator, Ordering ordering, String elementId) {
        super();
        this.operator = operator;
        this.ordering = ordering;
        this.elementId = elementId;
    }

    public boolean isDefault() {
        return this == DEFAULT;
    }

    public static CollectionVerificationConfig getVerificationConfigFrom(Collection<?> collection, String field) {
        if (collection == null || collection.isEmpty()) {
            return DEFAULT;
        }
        Object firstElement = collection.iterator().next();
        if (firstElement instanceof CollectionVerificationConfig) {
            return (CollectionVerificationConfig) firstElement;
        }
        if (firstElement instanceof Map<?, ?>) {
            CollectionVerificationConfig config = new CollectionVerificationConfig();
            Map<?, ?> firstMap = (Map<?, ?>) firstElement;
            if (firstMap.containsKey(OPERATOR_FIELD_NAME) || firstMap.containsKey(ORDERING_FIELD_NAME)
                    || firstMap.containsKey(ELEMENT_ID_FIELD_NAME)) {
                Operator operator = Operator.of((String) firstMap.get(OPERATOR_FIELD_NAME));
                if (operator != null) {
                    config.setOperator(operator);
                }
                Ordering ordering = Ordering.of((String) firstMap.get(ORDERING_FIELD_NAME));
                if (ordering != null) {
                    config.setOrdering(ordering);
                } else {
                    config.setOrdering(BeftaMain.getConfig().getDefaultCollectionAssertionMode());
                }

                if (config.getOrdering() == Ordering.UNORDERED) {
                    String idString = (String) firstMap.get(ELEMENT_ID_FIELD_NAME);

                    if (idString != null) {
                        config.setElementId(idString);
                    } else {
                        config.setElementId(ElementIdFinder.findElementIds(collection, field));
                    }
                }
                return config;
            } else {
                config.setOrdering(BeftaMain.getConfig().getDefaultCollectionAssertionMode());

                if (config.getOrdering() == Ordering.UNORDERED) {
                        config.setElementId(ElementIdFinder.findElementIds(collection, field));
                }

                return config;
            }
        }
        return DEFAULT;
    }

    public static boolean isFirstElementOfCollectionMetadata(Object obj) {
        boolean returnValue = false;
        if (isFirstElementOfMapMetadata(obj)) {
            returnValue = true;
        } else if (obj instanceof Collection) {
            Iterator itr = ((Collection)obj).iterator();
            returnValue = itr.hasNext() && isFirstElementOfMapMetadata(itr.next());
         }
        return returnValue;
    }

    private static boolean isFirstElementOfMapMetadata(Object map) {
        if (map instanceof Map) {
            Map<String, String> mapInstance = (Map)map;
            return mapInstance.keySet().stream().anyMatch(element -> element.startsWith("__"));
        } else {
            return false;
        }
    }
}
