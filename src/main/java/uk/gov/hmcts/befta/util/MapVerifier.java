package uk.gov.hmcts.befta.util;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static uk.gov.hmcts.befta.util.MapVerifier.Matchers.newMatchers;

public class MapVerifier {

    private String fieldPrefix;

    private int maxMessageDepth;

    private boolean caseSensitiveForStrings;

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

    public MapVerificationResult verifyMap(Map<String, Object> expectedMap,
            Map<String, Object> actualMap) {
        return verifyMap(fieldPrefix, expectedMap, actualMap, 0, null);
    }

    private static final String OPERATOR_KEY = "operator";
    private static final String ORDERING_KEY = "ordering";
    private static final String ELEMENT_ID_KEY = "elemenet-id";
    enum Operator {
        EQUIVALENT_OF("equivalent_of"), SUPERSET_OF("superset_of"), SUBSET_OF("subset_of");

        private String name;

        Operator(final String name) {
            this.name = name;
        }
    }

    enum Ordering {
        ORDERED, UNORDERED
    }

    static class Matchers {
        private Operator operator;
        private Ordering ordering;
        private String[] elementId;

        private Matchers () {}

        public static Matchers newMatchers(String operator, String ordering, String elementId) {
            Matchers matchers = new Matchers();
            switch (operator) {
                case "superset-of":
                    matchers.operator = Operator.SUPERSET_OF;
                    break;
                case "subset-of":
                    matchers.operator = Operator.SUBSET_OF;
                    break;
                case "equivalent-of":
                    matchers.operator = Operator.EQUIVALENT_OF;
                    break;
            }
            matchers.ordering = Ordering.valueOf(ordering.toUpperCase());
            matchers.elementId = Optional.ofNullable(elementId).map(elements -> elements.split(",")).orElse(new String[0]);
            return matchers;
        }

        public boolean isSupersetOf() {
            return operator == Operator.SUPERSET_OF;
        }

        public boolean isSubsetOf() {
            return operator == Operator.SUBSET_OF;
        }

        public boolean isEquivalentOf() {
            return operator == Operator.EQUIVALENT_OF;
        }

        public boolean isOrdered() {
            return ordering == Ordering.ORDERED;
        }

        public boolean isUnordered() {
            return ordering == Ordering.UNORDERED;
        }

        public static Matchers empty() {
            return new Matchers();
        }
    }

    private MapVerificationResult verifyMap(String fieldPrefix, Map<String, Object> expectedMap,
        Map<String, Object> actualMap, int currentDepth, Matchers matchers) {

        boolean shouldReportAnyDifference = currentDepth <= maxMessageDepth;

        if (expectedMap == actualMap) {
            return MapVerificationResult.DEFAULT_VERIFIED;
        } else if (expectedMap == null) {
            return new MapVerificationResult(fieldPrefix, false,
                shouldReportAnyDifference ? "Map is expected to be null, but is actually not." : null, currentDepth,  maxMessageDepth);
        } else if (actualMap == null) {
            return new MapVerificationResult(fieldPrefix, false,
                    shouldReportAnyDifference ? "Map is expected to be non-null, but is actually null." : null,
                    currentDepth, maxMessageDepth);
        }

        List<String> unexpectedFields = checkForUnexpectedlyAvailableFields(expectedMap, actualMap);
        List<String> unavailableFields = checkForUnexpectedlyUnavailableFields(expectedMap, actualMap);
        List<MapVerificationResult> badSubmaps = Lists.newArrayList();
        List<String> badValueMessages = Lists.newArrayList();
        badValueMessages = collectBadValueMessagesFromMap(expectedMap,
                                                                       actualMap,
                                                                       fieldPrefix,
                                                                       currentDepth,
                                                                       maxMessageDepth,
                                                                       matchers);
        badSubmaps = collectBadSubMaps(expectedMap, actualMap, fieldPrefix,
                                                                   currentDepth);

        if (unexpectedFields.size() == 0 && unavailableFields.size() == 0 && badValueMessages.size() == 0
                && badSubmaps.size() == 0) {
            return MapVerificationResult.minimalVerifiedResult(fieldPrefix, currentDepth, maxMessageDepth);
        }
        return new MapVerificationResult(fieldPrefix, false, null, unexpectedFields,
                    unavailableFields,
                    badValueMessages, badSubmaps, currentDepth, maxMessageDepth);
    }

    @SuppressWarnings("unchecked")
    private List<MapVerificationResult> collectBadSubMaps(Map<String, Object> expectedMap,
                          Map<String, Object> actualMap, String fieldPrefix, int currentDepth) {
        ArrayList<MapVerificationResult> differences = new ArrayList<>();
        expectedMap.keySet().stream().filter(actualMap::containsKey)
                .forEach(commonKey -> {
                    Object expectedValue = expectedMap.get(commonKey);
                    Object actualValue = actualMap.get(commonKey);
                    if (expectedValue instanceof Map && actualValue instanceof Map) {
                        MapVerificationResult subResult = verifyMap(fieldPrefix + "." + commonKey,
                                                                    (Map<String, Object>) expectedValue,
                                                                    (Map<String, Object>) actualValue,
                                                                    currentDepth + 1,
                                                                    Matchers.empty());
                        if (!subResult.isVerified()) {
                            differences.add(subResult);
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
        return expectedMap.keySet().stream().filter(keyOfExpected -> !actualMap.containsKey(keyOfExpected)
                && isExpectedToBeAvailableInActual(expectedMap.get(keyOfExpected))).collect(Collectors.toList());
    }

    private List<String> collectBadValueMessagesFromMap(Map<String, Object> expectedMap,
            Map<String, Object> actualMap,
            String fieldPrefix, int currentDepth, int maxMessageDepth,
            Matchers matchers) {
        List<String> badValueMessages = new ArrayList<>();
        expectedMap.keySet().stream().filter(actualMap::containsKey)
                .forEach(commonKey -> {
                    Object expectedValue = expectedMap.get(commonKey);
                    Object actualValue = actualMap.get(commonKey);
                    if (expectedValue != actualValue) {
                        if (expectedValue == null) {
                            badValueMessages.add("Must be null: " + commonKey);
                        } else if (actualValue == null && isNonNullablePlaceholder(expectedValue)) {
                            badValueMessages.add("Must not be null: " + commonKey);
                        } else if (!(expectedValue instanceof Map && actualValue instanceof Map)) {
                            if (expectedValue instanceof Collection<?> && actualValue instanceof Collection<?>) {
                                collectBadValueMessagesFromCollection(fieldPrefix + "." + commonKey,
                                                                      commonKey,
                                                                      (Collection<?>) expectedValue,
                                                                      (Collection<?>) actualValue,
                                                                      currentDepth,
                                                                      maxMessageDepth,
                                                                      badValueMessages,
                                                                      matchers);
                            } else {
                                Object outcome = compareValues(commonKey, expectedValue, actualValue,
                                    currentDepth, maxMessageDepth);
                                if (!Boolean.TRUE.equals(outcome)) {
                                    if (outcome instanceof String) {
                                        badValueMessages.add((String) outcome);
                                    } else {
                                        badValueMessages.add(fieldPrefix + "." + commonKey);
                                    }
                                }
                            }
                        }
                    }
                });
        return badValueMessages;
    }

    @SuppressWarnings("unchecked")
    private void collectBadValueMessagesFromCollection(String fieldPrefix, String field,
                                                       Collection<?> expectedCollection, Collection<?> actualCollection,
                                                       int currentDepth, int maxMessageDepth,
                                                       List<String> badValueMessages, Matchers matchers) {
        Iterator<?> e1 = expectedCollection.iterator();
        Optional<Matchers> optionalMatchers = returnMatchersIfExist((LinkedHashMap) e1.next());
        if (optionalMatchers.isPresent() && optionalMatchers.get().isUnordered()) {
            e1.remove();
            if (optionalMatchers.get().isSubsetOf()) {
                Set expectedSet = new HashSet(expectedCollection);
                Set actualSet = new HashSet(actualCollection);
                // this does not take into account nested collections yet
                if (!actualSet.containsAll(expectedSet)) {
                    badValueMessages.add(fieldPrefix + " is not a subset.");
                }
            }
            if (optionalMatchers.get().isSupersetOf()) {
                Set expectedSet = new HashSet(expectedCollection);
                Set actualSet = new HashSet(actualCollection);
                // this does not take into account nested collections yet
                if (!expectedSet.containsAll(actualSet)) {
                    badValueMessages.add(fieldPrefix + " is not a superset.");
                }
            }
        }

        Iterator<?> e2 = actualCollection.iterator();
        int i = 0;
        if (isEquivalentOf(optionalMatchers) && expectedCollection.size() != actualCollection.size()) {
            badValueMessages.add(fieldPrefix + " has unexpected number of elements. Expected: "
                    + expectedCollection.size() + ", but actual: " + actualCollection.size() + ".");
        }
        while (e1.hasNext() && e2.hasNext()) {
            Object o1 = e1.next();
            Object o2 = e2.next();
            String subField = field + "[" + i + "]";
            if (o1 instanceof Map && o2 instanceof Map) {
                MapVerificationResult subResult = verifyMap(subField, (Map<String, Object>) o1,
                        (Map<String, Object>) o2,
                        currentDepth + 1,
                        optionalMatchers.orElseGet(Matchers::empty));
                if (!subResult.isVerified()) {
                    badValueMessages.addAll(subResult.getAllIssues());
                }
            } else if (o1 instanceof Collection && o2 instanceof Collection) {
                collectBadValueMessagesFromCollection(fieldPrefix,
                                                      subField,
                                                      (Collection<?>) o1,
                                                      (Collection<?>) o2,
                                                      currentDepth + 1,
                                                      maxMessageDepth,
                                                      badValueMessages,
                                                      matchers);
            } else {
                Object outcome = compareValues(subField, o1, o2, currentDepth + 1, maxMessageDepth);
                if (!Boolean.TRUE.equals(outcome)) {
                    if (outcome instanceof String) {
                        badValueMessages.add((String) outcome);
                    } else {
                        badValueMessages.add(subField);
                    }
                }

            }
            i++;
        }
    }

    private boolean isEquivalentOf(final Optional<Matchers> optionalMatchers) {
        return !optionalMatchers.isPresent() || optionalMatchers.get().isEquivalentOf();
    }

    private Optional<Matchers> returnMatchersIfExist(final LinkedHashMap firstElementFromExpected) {
        Optional<Matchers> optionalMatchers = Optional.empty();
        if (hasAnyCollectionMatchers( firstElementFromExpected)) {
            String operator = (String) firstElementFromExpected.get(OPERATOR_KEY);
            String ordering = (String) firstElementFromExpected.get(ORDERING_KEY);
            String elementId = (String) firstElementFromExpected.get(ELEMENT_ID_KEY);
            optionalMatchers = Optional.of(newMatchers(operator, ordering, elementId));
        }
        return optionalMatchers;
    }

    private boolean hasAnyCollectionMatchers(final LinkedHashMap firstElementFromExpected) {
        return firstElementFromExpected.get(OPERATOR_KEY) != null
        || firstElementFromExpected.get(ORDERING_KEY) != null
        || firstElementFromExpected.get(ELEMENT_ID_KEY) != null;
    }

    private Object compareValues(String commonKey, Object expectedValue,
            Object actualValue, int currentDepth, int maxMessageDepth) {
        boolean justCompare = currentDepth > maxMessageDepth;
        if(actualAcceptedByPlaceholder(expectedValue, actualValue)) {
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
            ExpectedValuePlaceholder expectedValuePlaceholder = ExpectedValuePlaceholder.getByValue((String) expectedValue);
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
        return expectedValuePlaceholder != null && expectedValuePlaceholder.isNullable();
    }

    private boolean isNonNullablePlaceholder(Object value) {
        if (!(value instanceof String)) {
            return false;
        }
        ExpectedValuePlaceholder expectedValuePlaceholder = ExpectedValuePlaceholder.getByValue((String) value);
        return expectedValuePlaceholder != null && !expectedValuePlaceholder.isNullable();
    }

    private boolean actualAcceptedByPlaceholder(Object expectedValue, Object actualValue) {
        if (expectedValue instanceof String) {
            ExpectedValuePlaceholder expectedValuePlaceholder = ExpectedValuePlaceholder.getByValue((String) expectedValue);
            if (expectedValuePlaceholder != null) {
                return expectedValuePlaceholder.accepts(actualValue);
            }
        }
        return false;
    }
}
