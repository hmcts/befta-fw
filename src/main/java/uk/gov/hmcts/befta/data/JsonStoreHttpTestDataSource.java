package uk.gov.hmcts.befta.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.reflect.ClassPath;

import java.util.ArrayList;

import uk.gov.hmcts.jsonstore.JsonResourceStoreWithInheritance;

public class JsonStoreHttpTestDataSource implements HttpTestDataSource {

    private Logger logger = LoggerFactory.getLogger(JsonStoreHttpTestDataSource.class);

    private ArrayList<String> resourcePaths = new ArrayList<>();

    private JsonResourceStoreWithInheritance jsonStore = null;

    public JsonStoreHttpTestDataSource(String[] resourcePackages) {
        long start = System.currentTimeMillis();
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
        } finally {
            long finish = System.currentTimeMillis();
            double seconds = (finish - start) / 1000.0;
            logger.info("Located {} test data resource files in {} seconds.", resourcePaths.size(), seconds);
        }
    }

    @Override
    public HttpTestData getDataForTestCall(String testDataId) {
        long start = System.currentTimeMillis();
        if (jsonStore == null) {
            jsonStore = new JsonResourceStoreWithInheritance(resourcePaths.toArray(new String[0]));
        }
        try {
            return jsonStore.getObjectWithId(testDataId, HttpTestData.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            long finish = System.currentTimeMillis();
            double seconds = (finish - start) / 1000.0;
            logger.info("Fetched test data for {} in {} seconds.", testDataId, seconds);
        }
    }

}
