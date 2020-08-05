package uk.gov.hmcts.befta.util;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReflectionUtils {

    public static Object deepGetFieldInObject(Object object, String fieldPath) throws Exception {
        if (object == null) {
            return null;
        }
        if (fieldPath == null || fieldPath.length() == 0) {
            throw new IllegalArgumentException("Field path must be non-empty String.");
        }
        final String regex = "(?<!\\\\)(?:\\\\\\\\)*\\."; // i.e. look for '.' but not the escaped version '\.'
        String[] rawFields = fieldPath.split(regex);
        // unescape fields
        List<String> fields = Arrays.stream(rawFields)
                .map(field -> field.replace("\\.", "."))
                .collect(Collectors.toList());

        Object fieldValue = retrieveFieldInObject(object, fields.get(0));
        for (int i = 1; i < fields.size(); i++) {
            fieldValue = retrieveFieldInObject(fieldValue, fields.get(i));
        }
        return fieldValue;
    }

    public static Object retrieveFieldInObject(Object object, String fieldName) throws Exception {
        if (object == null)
            return null;
        if (fieldName == null || fieldName.length() == 0)
            throw new IllegalArgumentException("fieldName must be non-empty String.");

        int fieldIndex = -1;
        if (fieldName.contains("[")) {
            fieldIndex = Integer.parseInt(fieldName.substring(fieldName.indexOf("[") + 1, fieldName.length() - 1));
            fieldName = fieldName.substring(0, fieldName.indexOf("["));
        }

        String getterName = "get" + StringUtils.firstLetterToUpperCase(fieldName);

        Throwable thrown = null;
        Object valueFound = null;
        try {
            Method method = object.getClass().getMethod(getterName);
            valueFound = method.invoke(object);
        } catch (Throwable t) {
            thrown = t;
        }
        if (object instanceof Map)
            valueFound = ((Map<?, ?>) object).get(fieldName);
        else if (thrown != null)
            throw new NoSuchFieldException(fieldName + " not retrievable from " + object + ".");

        return getValueToReturnFrom(valueFound, fieldIndex);
    }

    private static Object getValueToReturnFrom(Object valueFound, int fieldIndex) {
        if (fieldIndex < 0)
            return valueFound;
        else {
            if (valueFound instanceof List<?>)
                return ((List<?>) valueFound).get(fieldIndex);
            else if (valueFound instanceof Object[])
                return ((Object[]) valueFound)[fieldIndex];
            else if (valueFound instanceof Iterable<?>) {
                Iterator<?> itr = ((Iterable<?>) valueFound).iterator();
                int i = 0;
                while (itr.hasNext()) {
                    Object element = itr.next();
                    if (i == fieldIndex)
                        return element;
                    i++;
                }
                throw new RuntimeException(
                        "Element " + fieldIndex + " not available. Size of the containing object is " + i + ".");
            }
            else {
                throw new RuntimeException(
                        "Element " + fieldIndex + " not addressable on " + valueFound + ".");
            }
        }
    }

    public static class DummyPlaceHolder {
    }

    @SuppressWarnings({ "rawtypes" })
    public static boolean mapContains(Map<?, ?> big, Map<?, ?> small) {
        if (big == null)
            return small == null;
        else if (small == null)
            throw new IllegalArgumentException("Small cannot be null when big is not.");

        if (small.size() > big.size())
            return false;
        for (Object key : small.keySet()) {
            Object smallValue = small.get(key);
            Object bigValue = big.get(key);
            if (smallValue instanceof Map) {
                boolean valueContained = mapContains((Map) bigValue, (Map) smallValue);
                if (!valueContained)
                    return false;
            } else if (small != null) {
                boolean valueEquals = smallValue.equals(bigValue);
                if (!valueEquals)
                    return false;
            }
        }
        return true;
    }

}
