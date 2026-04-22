package uk.gov.hmcts.jsonstore;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.File;

public class JsonFileStoreWithInheritance extends JsonStoreWithInheritance {

    private File location;
    private ObjectMapper mapper = new ObjectMapper();

    public JsonFileStoreWithInheritance(File location) {
        super();
        this.location = location;
    }

    public JsonFileStoreWithInheritance(File location, String idFieldName, String inheritanceFieldName) {
        super(idFieldName, inheritanceFieldName);
        this.location = location;
    }

    @Override
    protected void buildObjectStore() throws Exception {
        rootNode = buildObjectStoreIn(location);
    }

    private JsonNode buildObjectStoreIn(File location) throws Exception {
        if (!location.isDirectory())
            return buildObjectStoreInAFile(location);
        File[] subfiles = location.listFiles();
        ArrayNode store = new ArrayNode(null);
        for (File subfile : subfiles) {
            JsonNode substore = null;
            boolean isJsonFile = false;
            if (subfile.isDirectory())
                substore = buildObjectStoreIn(subfile);
            else if (subfile.getName().toLowerCase().endsWith(".json")) {
                substore = buildObjectStoreInAFile(subfile);
                isJsonFile = true;
            }

            if (substore != null && isJsonFile) {
                String guid = substore.get(GUID).asText();
                validateGUID(guid, subfile.getPath());
                if (substore.isArray()) {
                    for (int i = 0; i < substore.size(); i++)
                        store.add(substore.get(i));
                } else
                    store.add(substore);
                processedGUIDs.add(guid);
                guidSources.put(guid, subfile.getPath());
            } else if (substore != null) {
                if (substore.isArray()) {
                    for (int i = 0; i < substore.size(); i++)
                        store.add(substore.get(i));
                } else {
                    store.add(substore);
                }
            }
        }
        throwIfDuplicateGUIDsFound();
        if (store.size() == 1)
            return store.get(0);
        return store;
    }

    private JsonNode buildObjectStoreInAFile(File file) throws Exception {
        return mapper.readTree(file);
    }
}
