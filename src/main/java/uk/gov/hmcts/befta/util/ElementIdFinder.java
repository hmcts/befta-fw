package uk.gov.hmcts.befta.util;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import uk.gov.hmcts.befta.exception.InvalidTestDataException;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ElementIdFinder {

    private static final String COULD_NOT_CALCULATE_ELEMENT_ID = "Befta Framework could not calculate the element " +
            "IDs required to compare an undordered collection, please add an `__elementId__` to your expected " +
            "response body";

    private ElementIdFinder() {

    }

    private static boolean isCalculatedAtRuntime(String value) {
        List<String> elements = Lists.reverse(Arrays.asList(value.substring(value.indexOf("[") + 1).split("]\\[|]}")));

        boolean isCalculatedAtRuntime = false;
        for (int i = 0; i < elements.size(); i ++) {
            if (elements.get(i).toLowerCase().contains("testdata")) {
                if (!elements.get(i + 1).toLowerCase().contains("context")) {
                    isCalculatedAtRuntime = true;
                    break;
                }
            }
        }
        return isCalculatedAtRuntime;
    }

    /**
     * Returns unique element ids that can be used to compare unordered test data within a collection.
     *
     * Fields containing data calculated at runtime are excluded
     *
     * Throws an InvalidTestDataException if no element ids can be calculated
     *
     * @param expectedCollection the expected collection test data
     * @return an alphabetically sorted comma delimited string of elements IDs
     */
    public static String findElementIds(Collection<?> expectedCollection) {
        SortedSet<String> commonMapKeys;

        Iterator itr = expectedCollection.iterator();
        itr.next(); // first element in collection is the metadata map

        AtomicInteger elementsVisited = new AtomicInteger();

        Multimap<String, String> multimap = ArrayListMultimap.create();

        itr.forEachRemaining( currentElement -> {
            Map map = ((Map)currentElement);
            elementsVisited.getAndIncrement();
            Set<Map.Entry> keys = map.entrySet();
            keys.forEach(entry -> {
                String key = (String) entry.getKey();
                if (entry.getValue() instanceof String) { // Don't examine more 1 level deep
                    String value = (String) entry.getValue();
                    if (isCalculatedAtRuntime(value)) {
                        BeftaUtils.defaultLog(String.format("Ignoring map value calculated at runtime when finding " +
                                "elements for ordering %s: %s", key, value));
                    } else {
                        if (!multimap.containsEntry(key, value)) {
                            multimap.put(key, value);
                        }
                    }
                }
            });
        });

        commonMapKeys = multimap.keySet().stream()
                .filter(key -> multimap.get(key).size() > 1 && multimap.get(key).size() == elementsVisited.get())
                .collect(Collectors.toCollection(TreeSet::new));

        if (commonMapKeys.isEmpty()) {
            throw new InvalidTestDataException(COULD_NOT_CALCULATE_ELEMENT_ID);
        }

        return String.join(",", commonMapKeys);
    }

}
