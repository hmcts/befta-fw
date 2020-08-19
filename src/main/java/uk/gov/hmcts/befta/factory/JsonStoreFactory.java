/**
 * 
 */
package uk.gov.hmcts.befta.factory;

import java.io.File;

import uk.gov.hmcts.befta.util.BeftaUtils;
import uk.gov.hmcts.jsonstore.JsonFileStoreWithInheritance;
import uk.gov.hmcts.jsonstore.JsonResourceStoreWithInheritance;
import uk.gov.hmcts.jsonstore.JsonStoreWithInheritance;

/**
 * @author korneleehenry
 *
 */
public class JsonStoreFactory {
	private JsonStoreFactory() {}
	public static final String FILE_STR = "FILE";
	public static JsonStoreWithInheritance createJsonStore(String jsonStoreOption,String[] resourcePaths, String idFieldName, String inheritanceFieldName) {
		if(FILE_STR.equalsIgnoreCase(jsonStoreOption)) {
			File location = BeftaUtils.getSingleFileFromResource(resourcePaths);
			return new JsonFileStoreWithInheritance(location, idFieldName, inheritanceFieldName);
		}
		else {
			return new JsonResourceStoreWithInheritance(resourcePaths, idFieldName, inheritanceFieldName);
		}
	}
	public static JsonStoreWithInheritance createJsonStore(String jsonStoreOption,String[] resourcePaths) {
		if(FILE_STR.equalsIgnoreCase(jsonStoreOption)) {
			File location = BeftaUtils.getSingleFileFromResource(resourcePaths);
			return new JsonFileStoreWithInheritance(location);
		}
		else {
			return new JsonResourceStoreWithInheritance(resourcePaths);
		}
	}
}
