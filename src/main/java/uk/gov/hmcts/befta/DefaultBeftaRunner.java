package uk.gov.hmcts.befta;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
        plugin = "json:target/cucumber.json",
        glue = "uk.gov.hmcts.befta.player",
        features = { "classpath:features" },
        tags = { "not @Ignore" }
)
public class DefaultBeftaRunner {

    private DefaultBeftaRunner() {
    }

    @BeforeClass
    public static void setUp() {
        BeftaMain.setUp();
    }

    @AfterClass
    public static void tearDown() {
        BeftaMain.tearDown();
    }

}
