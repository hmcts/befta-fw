package uk.gov.hmcts.befta;

import io.cucumber.core.cli.Main;
import uk.gov.hmcts.befta.player.DefaultBackEndFunctionalTestScenarioPlayer;
import uk.gov.hmcts.befta.util.CucumberStepAnnotationUtils;

public class BeftaMain {

    private static TestAutomationConfig config = TestAutomationConfig.INSTANCE;
    private static TestAutomationAdapter taAdapter = new DefaultTestAutomationAdapter();

    public static void main(String[] args) {
        BeftaMain.tearUp();
        Main.main(args);
    }

    public static void tearUp() {
        CucumberStepAnnotationUtils.injectCommonSyntacticFlexibilitiesIntoStepDefinitions(DefaultBackEndFunctionalTestScenarioPlayer.class);
        BeftaMain.getAdapter().loadTestDataIfNecessary();
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
