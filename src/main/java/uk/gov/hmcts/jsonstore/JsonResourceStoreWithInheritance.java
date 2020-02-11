package uk.gov.hmcts.jsonstore;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.Sets;

import java.util.Set;

public class JsonResourceStoreWithInheritance extends JsonStoreWithInheritance {
    private String[] resourcePaths;
    private ObjectMapper mapper = new ObjectMapper();

    public JsonResourceStoreWithInheritance(String[] resourcePaths) {
        super();
        this.resourcePaths = resourcePaths;
    }

    public JsonResourceStoreWithInheritance(String[] resourcePaths, String idFieldName, String inheritanceFieldName) {
        super(idFieldName, inheritanceFieldName);
        this.resourcePaths = resourcePaths;
    }

    @Override
    protected void buildObjectStore() throws Exception {
        rootNode = buildObjectStoreInResourcePaths();
    }

    private JsonNode buildObjectStoreInResourcePaths() throws Exception {
        ArrayNode store = new ArrayNode(null);
        for (String resource : resourcePaths) {
            JsonNode substore = null;
            if (resource.toLowerCase().endsWith(".json"))
                substore = buildObjectStoreInAResource(resource);
            if (substore != null) {
                String guid = substore.get(GUID).asText();
                validateGUIDs(guid);
                if (substore.isArray()) {
                    for (int i = 0; i < substore.size(); i++)
                        store.add(substore.get(i));
                } else
                    store.add(substore);
                processedGUIDs.add(guid);
            }
        }
        if (store.size() == 1)
            return store.get(0);
        return store;
    }

    private JsonNode buildObjectStoreInAResource(String resource) throws Exception {
        return mapper.readTree(this.getClass().getClassLoader().getResourceAsStream(resource));
    }

}
