package uk.gov.hmcts.befta.util;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public enum ExpectedValuePlaceholder {

    ANYTHING_PRESENT(false),

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
        if (actualObject == null) {
            return isNullable();
        }

        boolean valid = true;

        try {
            switch (this) {
                case ANY_STRING_NULLABLE:
                case ANY_STRING_NOT_NULLABLE:
                    valid = actualObject instanceof String;
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
                case ANY_DATE_NULLABLE:
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    simpleDateFormat.parse(actualObject.toString());
                    break;

                case ANY_TIMESTAMP_NOT_NULLABLE:
                case ANY_TIMESTAMP_NULLABLE:
                parseTimestamp(actualObject);
                    break;

                case ANY_NULLABLE:
                case ANY_NOT_NULLABLE:
                case ANYTHING_PRESENT:
                case ANY_OBJECT_NOT_NULLABLE :
                case ANY_OBJECT_NULLABLE:

            }
        } catch (Exception e) {
            valid = false;
        }

        return valid;
    }

    private void parseTimestamp(Object actualObject) throws ParseException {
        try {
            SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            timestampFormat.parse(actualObject.toString());
        } catch (ParseException e) {
            try {
                SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                timestampFormat.parse(actualObject.toString());
            } catch (ParseException ex) {
                    SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                    timestampFormat.parse(actualObject.toString());
            }
        }
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
