package uk.gov.hmcts.befta.util;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import uk.gov.hmcts.befta.exception.InvalidTestDataException;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class ElementIdFinder {

    private static final String COULD_NOT_CALCULATE_ELEMENT_ID = "Befta Framework could not calculate the element " +
            "IDs required to compare an undordered collection, please add an `__elementId__` to your expected response" +
            " body";

    public boolean isCalculatedAtRuntime(String value) {
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

    public Set<String> findCommonMapEntries(Collection<?> expectedCollection) {
        Set<String> commonMapKeys = new HashSet<>();

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
                    String value = (String) entry.getValue();
                    if (isCalculatedAtRuntime(value)) {
                        BeftaUtils.defaultLog("Ignoring map value calculated at runtime when finding elements for ordering");
                    } else {
                        if (!multimap.containsEntry(key, value)) {
                            multimap.put(key, value);
                        }
                    }
                });
        });

        multimap.keySet().stream().forEach(x -> {if (multimap.get(x).size() > 1 && multimap.get(x).size() == elementsVisited.get()) {
                commonMapKeys.add(x);
            }
        });

        if (commonMapKeys.isEmpty()) {
            throw new InvalidTestDataException(COULD_NOT_CALCULATE_ELEMENT_ID);
        }

        return commonMapKeys;
    }

}
