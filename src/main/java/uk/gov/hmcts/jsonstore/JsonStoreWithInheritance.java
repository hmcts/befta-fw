package uk.gov.hmcts.jsonstore;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Sets;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import uk.gov.hmcts.befta.exception.InvalidTestDataException;
import uk.gov.hmcts.befta.exception.ParentNotFoundException;
import uk.gov.hmcts.befta.util.ReflectionUtils;

public abstract class JsonStoreWithInheritance {

    private static final String INHERITANCE_APPLIED = "inheritanceApplied";
    private static final String REPLACE_ARRAY_CONTENT = "__befta_replace__";
    protected static final String GUID = "_guid_";
    protected static final String EXTENDS = "_extends_";

    protected JsonNode rootNode;
    protected Map<String, JsonNode> nodeLibrary = new HashMap<>();
    protected Map<Class<?>, Map<String, ?>> objectLibraryPerTypes = new HashMap<>();
    protected final String idFieldName;
    protected final String inheritanceFieldName;
    protected Set<String> processedGUIDs = Sets.newHashSet();

    public JsonStoreWithInheritance() {
        this(GUID, EXTENDS);
    }

    public JsonStoreWithInheritance(String idFieldName, String inheritanceFieldName) {
        this.idFieldName = idFieldName;
        this.inheritanceFieldName = inheritanceFieldName;
    }

    protected JsonNode getRootNode() throws Exception {
        if (rootNode == null)
            loadStore();
        return rootNode;
    }

    protected Map<String, JsonNode> getNodeLibrary() throws Exception {
        if (rootNode == null)
            loadStore();
        return nodeLibrary;
    }

    private void loadStore() throws Exception {
        try {
            buildObjectStore();
            addToLibrary(rootNode);
            for (String id : nodeLibrary.keySet())
                inheritAndOverlayValuesFor(nodeLibrary.get(id));
            removeInheritanceMechanismFields(rootNode);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @SuppressWarnings("unchecked")
    private <T> Map<String, T> getMapWithIds(Class<? extends T> clazz) throws Exception {
        Map<String, T> objectLibrary = (Map<String, T>) objectLibraryPerTypes.get(clazz);
        if (objectLibrary == null) {
            objectLibrary = new HashMap<String, T>();
            Set<String> keys = getNodeLibrary().keySet();
            ObjectMapper om = new ObjectMapper();
            om.enable(SerializationFeature.INDENT_OUTPUT);
            om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            for (String key : keys) {
                JsonNode nodeInLibrary = nodeLibrary.get(key);
                String jsonText = om.writeValueAsString(nodeInLibrary);
                T anOnject = om.readValue(jsonText, clazz);
                try {
                    if (ReflectionUtils.retrieveFieldInObject(anOnject, idFieldName) != null) {
                        objectLibrary.put(key, anOnject);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            objectLibraryPerTypes.put(clazz, objectLibrary);
        }
        return objectLibrary;
    }

    public <T> T getObjectWithId(String id, Class<? extends T> clazz) throws Exception {
        return getMapWithIds(clazz).get(id);
    }

    private void removeInheritanceMechanismFields(JsonNode node) {
        if (node.has(INHERITANCE_APPLIED)) {
            ((ObjectNode) node).remove(INHERITANCE_APPLIED);
        }
        if (node.has(inheritanceFieldName)) {
            ((ObjectNode) node).remove(inheritanceFieldName);
        }

        Iterator<JsonNode> fields = node.iterator();
        while (fields.hasNext())
            removeInheritanceMechanismFields(fields.next());
    }

    protected abstract void buildObjectStore() throws Exception;

    protected void validateGUID(String guid) {
        if (processedGUIDs.contains(guid))
            throw new InvalidTestDataException("Object with _guid_=" + guid + " already exists");
    }

    private void inheritAndOverlayValuesFor(JsonNode object) {
        if (!shouldApplyInheritanceOn(object))
            return;
        JsonNode parentIdField = object.get(inheritanceFieldName);
        if (parentIdField != null) {
            String parentId = parentIdField.asText();
            JsonNode parentNode = nodeLibrary.get(parentId);
            throwExceptionIfParentNotFound(object, parentNode, parentId);
            inheritAndOverlayValuesFor(parentNode);
            Iterator<String> parentIterator = parentNode.fieldNames();
            while (parentIterator.hasNext()) {
                String fieldNameInParent = parentIterator.next();
                if (!isInheritanceMechanismField(fieldNameInParent)) {
                    inheritAndOverlayValuesFor(parentNode.get(fieldNameInParent));
                    JsonNode parentFieldCopy = parentNode.get(fieldNameInParent).deepCopy();
                    inheritAndOverlayChildValuesFromParent(object, fieldNameInParent, parentFieldCopy);
                }
            }
        }

        for (final JsonNode jsonNode : object)
            inheritAndOverlayValuesFor(jsonNode);

        if (object instanceof ObjectNode)
            ((ObjectNode) object).set(INHERITANCE_APPLIED, BooleanNode.TRUE);
    }

    private void inheritAndOverlayChildValuesFromParent(final JsonNode object, final String fieldNameInParent,
            final JsonNode parentFieldCopy) {
        if (object.has(fieldNameInParent)) {
            JsonNode thisField = object.get(fieldNameInParent);
            if (thisField.isArray()) {
                ((ArrayNode) thisField).forEach(element -> {
                    inheritAndOverlayValuesFor(element);
                });
                if (thisField.size() > 0 && REPLACE_ARRAY_CONTENT.equalsIgnoreCase(thisField.get(0).asText())) {
                    ((ArrayNode) parentFieldCopy).removeAll();
                    for (int e = 0; e < thisField.size(); e++) {
                        ((ArrayNode) parentFieldCopy).add(thisField.get(e));
                    }
                } else {
                    ((ArrayNode) parentFieldCopy).addAll((ArrayNode) thisField);
                }
                ((ObjectNode) object).set(fieldNameInParent, parentFieldCopy);
            } else if (thisField.isContainerNode()) {
                inheritAndOverlayValuesFor(thisField);
                if (!(parentFieldCopy instanceof NullNode)) {
                    overlayFieldWith(parentFieldCopy, thisField);
                    ((ObjectNode) object).set(fieldNameInParent, parentFieldCopy);
                }
            }
        } else {
            ((ObjectNode) object).set(fieldNameInParent, parentFieldCopy);
        }
    }

    private boolean shouldApplyInheritanceOn(JsonNode object) {
        if (object == null || !object.isContainerNode())
            return false;
        if (object.has(INHERITANCE_APPLIED))
            return false;
        return true;
    }

    private void throwExceptionIfParentNotFound(JsonNode object, JsonNode parentNode, String parentId) {
        if (parentNode == null) {
            String idPart = "an object without a " + idFieldName + " value specified";
            if (object.has(idFieldName)) {
                idPart = object.get(idFieldName).asText();
            }
            throw new ParentNotFoundException("Parent object with key " + parentId + " not found for " + idPart + ".");
        }
    }

    private void overlayFieldWith(JsonNode overlaidField, JsonNode overlayingField) {
        if (overlayingField.isArray()) {
            if (overlayingField.size() > 0 && REPLACE_ARRAY_CONTENT.equalsIgnoreCase(overlayingField.get(0).asText())) {
                ((ArrayNode) overlaidField).removeAll();
                for (int e = 1; e < overlayingField.size(); e++) {
                    ((ArrayNode) overlaidField).add(overlayingField.get(e));
                }
            } else
                ((ArrayNode) overlaidField).addAll((ArrayNode) overlayingField);
        } else {
            Iterator<String> overlayingSubfields = overlayingField.fieldNames();
            while (overlayingSubfields.hasNext()) {
                String overlayingSubFieldName = overlayingSubfields.next();
                if (!isInheritanceMechanismField(overlayingSubFieldName)) {
                    JsonNode overlayingSubField = overlayingField.get(overlayingSubFieldName);
                    JsonNode overlaidSubField = overlaidField.get(overlayingSubFieldName);
                    if (overlaidSubField != null && overlaidSubField.isContainerNode()) {
                        overlayFieldWith(overlaidSubField, overlayingSubField);
                    } else if (overlaidField instanceof ObjectNode) {
                        ((ObjectNode) overlaidField).set(overlayingSubFieldName, overlayingSubField);
                    }
                }
            }
        }
    }

    private boolean isInheritanceMechanismField(String fieldName) {
        return fieldName.equalsIgnoreCase(idFieldName) || fieldName.equalsIgnoreCase(inheritanceFieldName)
                || fieldName.equalsIgnoreCase(INHERITANCE_APPLIED);
    }

    private void addToLibrary(JsonNode object) throws Exception {
        String keyFromIdField;
        if (shouldPlaceInLibrary(object)) {
            if (object.has(idFieldName)) {
                keyFromIdField = object.get(idFieldName).asText();
            } else {
                keyFromIdField = UUID.randomUUID().toString();
            }
            nodeLibrary.put(keyFromIdField, object);
        }
        Iterator<JsonNode> iterator = object.iterator();
        while (iterator.hasNext()) {
            addToLibrary(iterator.next());
        }
    }

    private boolean shouldPlaceInLibrary(JsonNode object) {
        return object.has(idFieldName) || object.has(inheritanceFieldName);
    }
}
