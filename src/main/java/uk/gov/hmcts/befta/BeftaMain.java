package uk.gov.hmcts.befta;

import io.cucumber.core.cli.Main;
import uk.gov.hmcts.befta.featuretoggle.FeatureToggleService;
import uk.gov.hmcts.befta.player.DefaultBackEndFunctionalTestScenarioPlayer;
import uk.gov.hmcts.befta.util.CucumberStepAnnotationUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static uk.gov.hmcts.befta.util.BeftaUtils.defaultLog;

public class BeftaMain {

    private static TestAutomationConfig config = TestAutomationConfig.INSTANCE;
    private static TestAutomationAdapter taAdapter = null;
    private static FeatureToggleService featureToggleService = FeatureToggleService.DEFAULT_INSTANCE;

    public static void main(String[] args) {
        main(args, new DefaultTestAutomationAdapter());
    }

    public static void main(String[] args, TestAutomationConfig config) {
        main(args, config, new DefaultTestAutomationAdapter());
    }

    public static void main(String[] args, TestAutomationAdapter taAdapter) {
        main(args, TestAutomationConfig.INSTANCE, taAdapter);
    }

    public static void main(String[] args, TestAutomationConfig config, TestAutomationAdapter taAdapter) {
        setUp(config, taAdapter);
        try {
            runCucumberMain(args);
        } finally {
            tearDown();
        }
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

    public static void setUp(TestAutomationConfig config, TestAutomationAdapter taAdapter) {
        setUp(TestAutomationConfig.INSTANCE, taAdapter, FeatureToggleService.DEFAULT_INSTANCE);
    }

    public static void setUp(TestAutomationConfig config, TestAutomationAdapter taAdapter,
            FeatureToggleService featureToggleService) {
        outputJarInformation(BeftaMain.class);
        setConfig(config);
        setTaAdapter(taAdapter);
        setFeatureToggleService(featureToggleService);
        CucumberStepAnnotationUtils.injectCommonSyntacticFlexibilitiesIntoStepDefinitions(
                DefaultBackEndFunctionalTestScenarioPlayer.class);
        getAdapter().getDataLoader().loadDataIfNotLoadedVeryRecently();
    }

    public static void tearDown() {

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

    public static FeatureToggleService getFeatureToggleService() {
        return featureToggleService;
    }

    public static void setFeatureToggleService(FeatureToggleService featureToggle) {
        BeftaMain.featureToggleService = featureToggle;
    }

    protected static void outputJarInformation(Class<?> clazz) {
        // regex to search only JAR paths ...
        // ... and extract the jar name (see output of first match group)
        final String regex = "^(?:jar:.*/)(.*\\.jar)(?:!.*)$";

        // load class path including jar information when packaged
        final String className = clazz.getSimpleName() + ".class";
        final String classPath = clazz.getResource(className).toString();

        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(classPath);

        if (matcher.find()) {
            defaultLog(format("Jar: %s", matcher.group(1)));
        }
    }

}
