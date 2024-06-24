package uk.gov.hmcts.befta.util;

import uk.gov.hmcts.befta.data.CollectionVerificationConfig;
import uk.gov.hmcts.befta.data.CollectionVerificationConfig.Ordering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class MapVerifier {

    private String fieldPrefix;

    private int maxMessageDepth;

    private boolean caseSensitiveForStrings;

	public static MapVerifier createMapVerifier(String fieldPrefix, int maxMessageDepth,
			boolean caseSensitiveForStrings) {
		return new MapVerifier(fieldPrefix, maxMessageDepth, caseSensitiveForStrings);
	}

	public static MapVerifier createMapVerifier(String fieldPrefix, int maxMessageDepth) {
		return new MapVerifier(fieldPrefix, maxMessageDepth);
	}

    public MapVerifier(String fieldPrefix) {
        this(fieldPrefix, 5);
    }

    public MapVerifier(String fieldPrefix, int maxMessageDepth) {
        this(fieldPrefix, maxMessageDepth, true);
    }

    public MapVerifier(String fieldPrefix, boolean caseSensitiveForStrings) {
        this(fieldPrefix, 5, caseSensitiveForStrings);
    }

    public MapVerifier(String fieldPrefix, int maxMessageDepth, boolean caseSensitiveForStrings) {
        super();
        if (maxMessageDepth < 0) {
            throw new IllegalArgumentException("Max depth cannot be negative.");
        }
        this.fieldPrefix = fieldPrefix;
        this.maxMessageDepth = maxMessageDepth;
        this.caseSensitiveForStrings = caseSensitiveForStrings;
    }

    public MapVerificationResult verifyMap(Map<String, Object> expectedMap, Map<String, Object> actualMap) {
        return verifyMap(fieldPrefix, expectedMap, actualMap, 0);
    }

    private MapVerificationResult verifyMap(String fieldPrefix, Map<String, Object> expectedMap,
            Map<String, Object> actualMap, int currentDepth) {

        boolean shouldReportAnyDifference = currentDepth <= maxMessageDepth;

        if (expectedMap == actualMap) {
            return MapVerificationResult.DEFAULT_VERIFIED;
        } else if (expectedMap == null) {
            return new MapVerificationResult(fieldPrefix, false,
                    shouldReportAnyDifference ? "Map is expected to be null, but is actually not." : null, currentDepth,
                    maxMessageDepth);
        } else if (actualMap == null) {
            return new MapVerificationResult(fieldPrefix, false,
                    shouldReportAnyDifference ? "Map is expected to be non-null, but is actually null." : null,
                    currentDepth, maxMessageDepth);
        }

        List<String> unexpectedFields = checkForUnexpectedlyAvailableFields(expectedMap, actualMap);
        List<String> unavailableFields = checkForUnexpectedlyUnavailableFields(expectedMap, actualMap);
        List<String> badValueMessages = collectBadValueMessagesFromMap(expectedMap, actualMap, fieldPrefix,
                currentDepth, maxMessageDepth);
        List<MapVerificationResult> badSubmaps = collectBadSubmaps(expectedMap, actualMap, fieldPrefix, currentDepth,
                maxMessageDepth);

        if (unexpectedFields.size() == 0 && unavailableFields.size() == 0 && badValueMessages.size() == 0
                && badSubmaps.size() == 0) {
            return MapVerificationResult.minimalVerifiedResult(fieldPrefix, currentDepth, maxMessageDepth);
        }
        return new MapVerificationResult(fieldPrefix, false, null, unexpectedFields, unavailableFields,
                badValueMessages, badSubmaps, currentDepth, maxMessageDepth);
    }

    @SuppressWarnings("unchecked")
    private List<MapVerificationResult> collectBadSubmaps(Map<String, Object> expectedMap,
            Map<String, Object> actualMap, String fieldPrefix, int currentDepth, int maxMessageDepth) {
        ArrayList<MapVerificationResult> differences = new ArrayList<>();
        expectedMap.keySet().stream().filter(keyOfExpected -> actualMap.containsKey(keyOfExpected))
                .forEach(commonKey -> {
                    Object expectedValue = expectedMap.get(commonKey);
                    Object actualValue = actualMap.get(commonKey);
                    if (expectedValue instanceof Map && actualValue instanceof Map) {
                        MapVerificationResult subresult = verifyMap(fieldPrefix + "." + commonKey,
                                (Map<String, Object>) expectedValue, (Map<String, Object>) actualValue,
                                currentDepth + 1);
                        if (!subresult.isVerified()) {
                            differences.add(subresult);
                        }
                    }
                });
        return differences;
    }

    private List<String> checkForUnexpectedlyAvailableFields(Map<String, Object> expectedMap,
            Map<String, Object> actualMap) {
        return actualMap.keySet().stream().filter(keyOfActual -> !expectedMap.containsKey(keyOfActual))
                .collect(Collectors.toList());
    }

    private List<String> checkForUnexpectedlyUnavailableFields(Map<String, Object> expectedMap,
            Map<String, Object> actualMap) {
        return expectedMap.keySet().stream()
                .filter(keyOfExpected -> !actualMap.containsKey(keyOfExpected)
                        && isExpectedToBeAvailableInActual(expectedMap.get(keyOfExpected)))
                .collect(Collectors.toList());
    }

    private List<String> collectBadValueMessagesFromMap(Map<String, Object> expectedMap, Map<String, Object> actualMap,
            String fieldPrefix, int currentDepth, int maxMessageDepth) {
        List<String> badValueMessages = new ArrayList<>();
        expectedMap.keySet().stream().filter(keyOfExpected -> actualMap.containsKey(keyOfExpected))
                .forEach(commonKey -> {
                    Object expectedValue = expectedMap.get(commonKey);
                    Object actualValue = actualMap.get(commonKey);
                    if (expectedValue == actualValue) {
                        //
                    } else if (expectedValue == null) {
                        badValueMessages.add("Must be null: " + commonKey);
                    } else if (actualValue == null && isNoneNullablePlaceholder(expectedValue)) {
                        badValueMessages.add("Must not be null: " + commonKey);
                    } else if (!(expectedValue instanceof Map && actualValue instanceof Map)) {
                        if (expectedValue instanceof Collection<?> && actualValue instanceof Collection<?>) {
                            collectBadValueMessagesFromCollection(fieldPrefix + "." + commonKey, commonKey,
                                    (Collection<?>) expectedValue, (Collection<?>) actualValue, currentDepth,
                                    maxMessageDepth, badValueMessages);
                        } else {
                            Object outcome = compareValues(commonKey, expectedValue, actualValue, currentDepth,
                                    maxMessageDepth);
                            if (!Boolean.TRUE.equals(outcome)) {
                                if (outcome instanceof String) {
                                    badValueMessages.add((String) outcome);
                                } else {
                                    badValueMessages.add(fieldPrefix + "." + commonKey);
                                }
                            }
                        }
                    }
                });
        return badValueMessages;
    }

    private void collectBadValueMessagesFromCollection(String fieldPrefix, String field,
            Collection<?> expectedCollection, Collection<?> actualCollection, int currentDepth, int maxMessageDepth,
            List<String> badValueMessages) {
        if (expectedCollection == actualCollection)
            return;

        CollectionVerificationConfig verificationConfig = CollectionVerificationConfig.getVerificationConfigFrom(expectedCollection, getFormattedField(fieldPrefix));
        addAnySizeBasedIssue(fieldPrefix, expectedCollection, actualCollection, verificationConfig, badValueMessages);
        Iterator<?> itrExpected = expectedCollection.iterator();
        if (CollectionVerificationConfig.isFirstElementOfCollectionMetadata(expectedCollection)) {
            itrExpected.next();
        }
        Iterator<?> itrActual = actualCollection.iterator();
        int i = 0;
        while (itrExpected.hasNext() && itrActual.hasNext()) {
            Object o1 = itrExpected.next();
            String subfield = getSubfieldFor(field, verificationConfig, i, o1);
            if (isPrimitive(o1)) {
                if (!actualCollection.contains(o1)) {
                    badValueMessages
                            .add(fieldPrefix + "." + subfield + " is expected to be " + o1 + ", but is not available.");
                }
            } else {
                Object o2 = pickItemToCompareWith(o1, actualCollection, itrActual, verificationConfig);
                applyVerificationOnCollectionElements(o1, o2, fieldPrefix, subfield, currentDepth, maxMessageDepth,
                        badValueMessages);
            }
            i++;
        }
    }

    private String getFormattedField(String fieldPrefix) {
        String formattedField;
        if (fieldPrefix.contains("_X_")) {
            formattedField = fieldPrefix.substring(0, fieldPrefix.indexOf('[')) + fieldPrefix.substring(fieldPrefix.indexOf(']') + 1);
        } else {
            formattedField = fieldPrefix.substring(fieldPrefix.indexOf('.') + 1);
        }
        return formattedField;
    }

    private String getSubfieldFor(String field, CollectionVerificationConfig verificationConfig, int index,
            Object objectWorkedOn) {
        if (verificationConfig.getOrdering() == Ordering.ORDERED || isPrimitive(objectWorkedOn))
            return field + "[" + index + "]";
        else
            return field + "[" + getIdValueIn(objectWorkedOn, verificationConfig.getElementId()) + "]";
    }

    private boolean isPrimitive(Object o) {
        return o == null || (o instanceof String) || (o instanceof Number);
    }

    private Object getIdValueIn(Object objectWorkedOn, String elementId) {
        try {
            String[] idElements = elementId.split(",");
            Object idElementValue = ReflectionUtils.deepGetFieldInObject(objectWorkedOn, idElements[0]);
            StringBuffer compositeIdValue = new StringBuffer(idElementValue.toString());
            for (int i = 1; i < idElements.length; i++)
                compositeIdValue.append("__X__")
                        .append(ReflectionUtils.deepGetFieldInObject(objectWorkedOn, idElements[i]));
            return compositeIdValue.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private void applyVerificationOnCollectionElements(Object o1, Object o2, String fieldPrefix2, String subfield,
            int currentDepth, int maxMessageDepth, List<String> badValueMessages) {
        if (o1 instanceof Map && o2 instanceof Map) {
            MapVerificationResult subresult = verifyMap(subfield, (Map<String, Object>) o1, (Map<String, Object>) o2,
                    currentDepth + 1);
            if (!subresult.isVerified()) {
                badValueMessages.addAll(subresult.getAllIssues());
            }
        } else if (o1 instanceof Collection && o2 instanceof Collection) {
            collectBadValueMessagesFromCollection(fieldPrefix, subfield, (Collection<?>) o1, (Collection<?>) o2,
                    currentDepth + 1, maxMessageDepth, badValueMessages);
        } else {
            Object outcome = compareValues(subfield, o1, o2, currentDepth + 1, maxMessageDepth);
            if (!Boolean.TRUE.equals(outcome)) {
                if (outcome instanceof String) {
                    badValueMessages.add((String) outcome);
                } else {
                    badValueMessages.add(subfield);
                }
            }

        }
    }

    private void addAnySizeBasedIssue(String fieldPrefix, Collection<?> expectedCollection,
            Collection<?> actualCollection, CollectionVerificationConfig verificationConfig,
            List<String> badValueMessages) {

        int expectedCount = expectedCollection.size() - (CollectionVerificationConfig.isFirstElementOfCollectionMetadata(expectedCollection) ? 1 : 0);
        int actualCount = actualCollection.size();

        switch (verificationConfig.getOperator()) {
        case EQUIVALENT:
            if (actualCount != expectedCount
                    && expectedCollection.size() != actualCollection.size()) {
                badValueMessages.add(fieldPrefix + " has unexpected number of elements. Expected: "
                        + expectedCollection.size() + ", but actual: " + actualCollection.size() + ".");
            }
            break;
        case SUBSET:
            if (actualCount > expectedCount
                    && expectedCollection.size() != actualCollection.size()) {
                badValueMessages.add(fieldPrefix + " has unexpected number of elements. Expected <= "
                        + expectedCollection.size() + ", but actual: " + actualCollection.size() + ".");
            }
            break;
        case SUPERSET:
            if (actualCount < expectedCount
                    && expectedCollection.size() != actualCollection.size()) {
                badValueMessages.add(fieldPrefix + " has unexpected number of elements. Expected >= "
                        + expectedCollection.size() + ", but actual: " + actualCollection.size() + ".");
            }
            break;
        }
    }

    private Object pickItemToCompareWith(Object o1, Collection<?> collection, Iterator<?> iterator,
            CollectionVerificationConfig verificationConfig) {
        if (verificationConfig.getOrdering() == Ordering.ORDERED)
            return iterator.next();
        else {
            if (isPrimitive(o1)) {
                return collection.stream().filter(element -> Objects.equals(element, o1)).findFirst().orElse(null);
            } else {
                return collection.stream()
                    .filter(element -> Objects.equals(getIdValueIn(element, verificationConfig.getElementId()),
                            getIdValueIn(o1, verificationConfig.getElementId())))
                    .findFirst().orElse(null);
            }
        }
    }

    private Object compareValues(String commonKey, Object expectedValue, Object actualValue, int currentDepth,
            int maxMessageDepth) {
        boolean justCompare = currentDepth > maxMessageDepth;
        if (actualAcceptedByPlaceholder(expectedValue, actualValue)) {
            return Boolean.TRUE;
        } else if (expectedValue == actualValue) {
            return Boolean.TRUE;
        } else if (expectedValue == null) {
            return justCompare ? Boolean.FALSE : "Must be null: " + commonKey;
        } else if (actualValue == null) {
            return justCompare ? canAcceptNullFor(expectedValue) : "Must be non-null: " + commonKey;
        } else {
            return compareNonNullLiteral(commonKey, expectedValue, actualValue, justCompare);
        }
    }

    private Object compareNonNullLiteral(String fieldName, Object expectedValue, Object actualValue,
            boolean justCompare) {
        if (!caseSensitiveForStrings && expectedValue instanceof String && actualValue instanceof String
                && ((String) expectedValue).equalsIgnoreCase((String) actualValue)) {
            return Boolean.TRUE;
        }
        if (expectedValue.equals(actualValue)) {
            return Boolean.TRUE;
        }
        return justCompare ? Boolean.FALSE
                : fieldName + ": expected '" + expectedValue + "' but got '" + actualValue + "'";
    }

    private boolean isExpectedToBeAvailableInActual(Object expectedValue) {
        if (expectedValue instanceof String) {
            ExpectedValuePlaceholder expectedValuePlaceholder = ExpectedValuePlaceholder
                    .getByValue((String) expectedValue);
            if (expectedValuePlaceholder != null) {
                return !expectedValuePlaceholder.isNullable();
            }
        }
        return true;
    }

    private Boolean canAcceptNullFor(Object expectedValue) {
        if (!(expectedValue instanceof String)) {
            return Boolean.FALSE;
        }
        return isNullablePlaceholder((String) expectedValue);
    }

    private boolean isNullablePlaceholder(String strExpectedValue) {
        ExpectedValuePlaceholder expectedValuePlaceholder = ExpectedValuePlaceholder.getByValue(strExpectedValue);
        return expectedValuePlaceholder == null ? true : expectedValuePlaceholder.isNullable();
    }

    private boolean isNoneNullablePlaceholder(Object value) {
        if (!(value instanceof String)) {
            return false;
        }
        ExpectedValuePlaceholder expectedValuePlaceholder = ExpectedValuePlaceholder.getByValue((String) value);
        return expectedValuePlaceholder == null ? true : !expectedValuePlaceholder.isNullable();
    }

    private boolean actualAcceptedByPlaceholder(Object expectedValue, Object actualValue) {
        if (expectedValue instanceof String) {
            ExpectedValuePlaceholder expectedValuePlaceholder = ExpectedValuePlaceholder
                    .getByValue((String) expectedValue);
            if (expectedValuePlaceholder != null) {
                return expectedValuePlaceholder.accepts(actualValue);
            }
        }
        return false;
    }
}
