package uk.gov.hmcts.befta.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

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

}
