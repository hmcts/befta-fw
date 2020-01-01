package uk.gov.hmcts.befta.data;

import com.google.common.reflect.ClassPath;

import java.util.ArrayList;

import uk.gov.hmcts.jsonstore.JsonResourceStoreWithInheritance;

public class JsonStoreHttpTestDataSource implements HttpTestDataSource {

    private ArrayList<String> resourcePaths = new ArrayList<>();

    private JsonResourceStoreWithInheritance jsonStore;

    public JsonStoreHttpTestDataSource(String[] resourcePackages) {
        try {
            ClassPath cp = ClassPath.from(Thread.currentThread().getContextClassLoader());
            for (String resourcePackage : resourcePackages) {
                String prefix = resourcePackage + "/";
                for (ClassPath.ResourceInfo info : cp.getResources()) {
                    if (info.getResourceName().startsWith(prefix)
                            && info.getResourceName().endsWith(".td.json")) {
                        resourcePaths.add(info.getResourceName());
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public HttpTestData getDataForTestCall(String testDataId) {
        loadDataStoreIfNotAlreadyLoaded();
        try {
            return jsonStore.getObjectWithId(testDataId, HttpTestData.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void loadDataStoreIfNotAlreadyLoaded() {
        jsonStore = new JsonResourceStoreWithInheritance(resourcePaths.toArray(new String[0]));
    }

}
