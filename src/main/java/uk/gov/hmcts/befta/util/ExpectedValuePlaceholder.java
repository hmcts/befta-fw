package uk.gov.hmcts.befta.util;

import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public enum ExpectedValuePlaceholder {

    ANYTHING_PRESENT(false),
    ANYTHING_IF_EXISTS(true),

    ANY_NULLABLE(true),
    ANY_STRING_NULLABLE(true, String.class),
    ANY_NUMBER_NULLABLE(true, Number.class),
    ANY_INTEGER_NULLABLE(true, Integer.class),
    ANY_FLOATING_NULLABLE(true, Float.class),
    ANY_DATE_NULLABLE(true,  Date.class),
    ANY_TIMESTAMP_NULLABLE(true, Timestamp.class),
    ANY_OBJECT_NULLABLE(true),

    ANY_NOT_NULLABLE(false),
    ANY_STRING_NOT_NULLABLE(false),
    ANY_NUMBER_NOT_NULLABLE(false),
    ANY_INTEGER_NOT_NULLABLE(false),
    ANY_FLOATING_NOT_NULLABLE(false),
    ANY_DATE_NOT_NULLABLE(false, Date.class),
    ANY_TIMESTAMP_NOT_NULLABLE(false, Timestamp.class),
    ANY_OBJECT_NOT_NULLABLE(false);


    private boolean nullable;
    private Class clazz;

    ExpectedValuePlaceholder(boolean nullable) {
        this(nullable, Object.class);
    }

    ExpectedValuePlaceholder(boolean nullable, Class clazz) {
        this.nullable = nullable;
        this.clazz = clazz;
    }

    public boolean isNullable() {
        return this.nullable;
    }

    public boolean isValid(Object actualObject) {
        if (isNullable() && actualObject == null) {
            return true;
        } else if (!isNullable() && actualObject == null) {
            return false;
        }
        boolean valid = true;
        if (actualObject != null) {
            try {
                if (this.clazz == String.class) {
                    valid &= actualObject instanceof String;
                } else if (this.clazz == Number.class) {
                    NumberFormat.getInstance().parse((String) actualObject);
                } else if (this.clazz == Integer.class) {
                    Integer.parseInt((String) actualObject);
                } else if (this.clazz == Float.class) {
                    Float.parseFloat((String) actualObject);
                } else if (this.clazz == Date.class) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    simpleDateFormat.parse((String) actualObject);
                } else if (this.clazz == Timestamp.class) {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                    simpleDateFormat.parse((String) actualObject);
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
