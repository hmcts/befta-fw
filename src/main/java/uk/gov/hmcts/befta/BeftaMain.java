package uk.gov.hmcts.befta;

import io.cucumber.core.cli.Main;
import uk.gov.hmcts.befta.player.DefaultBackEndFunctionalTestScenarioPlayer;
import uk.gov.hmcts.befta.util.CucumberStepAnnotationUtils;

public class BeftaMain {

    private static TestAutomationConfig config = TestAutomationConfig.INSTANCE;
    private static TestAutomationAdapter taAdapter = null;

    public static void main(String[] args) {
        main(args, new DefaultTestAutomationAdapter());
    }

    public static void main(String[] args, TestAutomationAdapter taAdapter) {
        setUp(taAdapter);
        runCucumberMain(args);
        tearDown();
    }


    public static void runCucumberMain(String[] args) {
        Main.main(args);
    }

    public static void setUp() {
        setUp(TestAutomationConfig.INSTANCE, new DefaultTestAutomationAdapter());
    }

    public static void setUp(TestAutomationConfig config) {
        setUp(config, new DefaultTestAutomationAdapter());
    }

    public static void setUp(TestAutomationAdapter taAdapter) {
        setUp(TestAutomationConfig.INSTANCE, taAdapter);
    }

    public static void tearDown() {

    }

    public static void setUp(TestAutomationConfig config, TestAutomationAdapter taAdapter) {
        setConfig(config);
        setTaAdapter(taAdapter);
        CucumberStepAnnotationUtils.injectCommonSyntacticFlexibilitiesIntoStepDefinitions(DefaultBackEndFunctionalTestScenarioPlayer.class);
        getAdapter().loadTestDataIfNecessary();
    }

    public static TestAutomationAdapter getAdapter() {
        return taAdapter;
    }

    public static void setTaAdapter(TestAutomationAdapter taAdapter) {
        BeftaMain.taAdapter = taAdapter;
    }

    public static TestAutomationConfig getConfig() {
        return config;
    }

    public static void setConfig(TestAutomationConfig config) {
        BeftaMain.config = config;
    }

}
