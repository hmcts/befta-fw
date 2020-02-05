package uk.gov.hmcts.befta.util;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;

public enum ExpectedValuePlaceholder {

    ANYTHING_PRESENT(false),
    ANYTHING_IF_EXISTS(true),

    ANY_NULLABLE(true),
    ANY_STRING_NULLABLE(true),
    ANY_NUMBER_NULLABLE(true),
    ANY_INTEGER_NULLABLE(true),
    ANY_FLOATING_NULLABLE(true),
    ANY_DATE_NULLABLE(true),
    ANY_TIMESTAMP_NULLABLE(true),
    ANY_OBJECT_NULLABLE(true),

    ANY_NOT_NULLABLE(false),
    ANY_STRING_NOT_NULLABLE(false),
    ANY_NUMBER_NOT_NULLABLE(false),
    ANY_INTEGER_NOT_NULLABLE(false),
    ANY_FLOATING_NOT_NULLABLE(false),
    ANY_DATE_NOT_NULLABLE(false),
    ANY_TIMESTAMP_NOT_NULLABLE(false),
    ANY_OBJECT_NOT_NULLABLE(false);


    private boolean nullable;

    ExpectedValuePlaceholder(boolean nullable) {
        this.nullable = nullable;
    }

    public boolean isNullable() {
        return this.nullable;
    }

    public boolean accepts(Object actualObject) {
        if (isNullable() && actualObject == null) {
            return true;
        } else if (!isNullable() && actualObject == null) {
            return false;
        }
        boolean valid = true;
        if (actualObject != null) {
            try {
                switch (this) {
                    case ANY_STRING_NULLABLE:
                    case ANY_STRING_NOT_NULLABLE:
                        valid &= actualObject instanceof String;
                        break;
                    case ANY_INTEGER_NOT_NULLABLE:
                    case ANY_INTEGER_NULLABLE:
                        Integer.parseInt(actualObject.toString());
                        break;
                    case ANY_NUMBER_NOT_NULLABLE:
                    case ANY_NUMBER_NULLABLE:
                        NumberFormat.getInstance().parse(actualObject.toString());
                        break;
                    case ANY_FLOATING_NOT_NULLABLE:
                    case ANY_FLOATING_NULLABLE:
                        Float.parseFloat(actualObject.toString());
                        break;
                    case ANY_DATE_NOT_NULLABLE:
                    case ANY_DATE_NULLABLE: {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        simpleDateFormat.parse(actualObject.toString());
                        break;
                    }
                    case ANY_TIMESTAMP_NOT_NULLABLE:
                    case ANY_TIMESTAMP_NULLABLE: {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                        simpleDateFormat.parse(actualObject.toString());
                        break;
                    }
                }
            } catch (Exception e) {
                valid &= false;
            }
        }
        return valid;
    }

    public String getValue() {
        return "[[" + name() + "]]";
    }

    public static ExpectedValuePlaceholder getByValue(String strValue) {
        ExpectedValuePlaceholder[] values = values();
        for(ExpectedValuePlaceholder value: values) {
            if (strValue.equalsIgnoreCase(value.getValue())) {
                return value;
            }
        }
        return null;
    }
}
