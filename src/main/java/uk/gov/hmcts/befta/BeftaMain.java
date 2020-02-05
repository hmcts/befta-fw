package uk.gov.hmcts.befta;

import uk.gov.hmcts.befta.player.DefaultBackEndFunctionalTestScenarioPlayer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

import io.cucumber.core.cli.Main;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class BeftaMain {
    private static TestAutomationConfig config = TestAutomationConfig.INSTANCE;
    private static TestAutomationAdapter taAdapter = new DefaultTestAutomationAdapter();

    public static void main(String[] args) {

        try {
            prepareCucumberStepAnnotations();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        BeftaTestDataLoader.main(args);
        Main.main(args);
    }

    private static void prepareCucumberStepAnnotations() throws NoSuchFieldException, IllegalAccessException {
        java.lang.reflect.Method[] methods = DefaultBackEndFunctionalTestScenarioPlayer.class.getMethods();

        for (java.lang.reflect.Method m : methods) {
            Annotation[] annotations = m.getDeclaredAnnotations();

            for (Annotation a : annotations) {
                if (a instanceof Given) {
                    Given newGiven = newGiven((Given) a);
                    replaceAnnotationClass(m, newGiven, Given.class);

                } else if (a instanceof Then.Thens) {
                    Then.Thens newThens = newThens((Then.Thens) a);
                    replaceAnnotationClass(m, newThens, Then.Thens.class);

                } else if (a instanceof When) {
                    When newWhen = newWhen((When) a);
                    replaceAnnotationClass(m, newWhen, When.class);

                } else if (a instanceof Then) {
                    Then newThen = newThen((Then) a);
                    replaceAnnotationClass(m, newThen, Then.class);
                }
            }
        }
    }

    private static Then.Thens newThens(final Then.Thens a) {
        return new Then.Thens() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return Then.Thens.class;
            }

            @Override
            public Then[] value() {
                Then[] oldValue = a.value();
                for (int i = 0; i < oldValue.length; i++) {
                    oldValue[i] = newThen(oldValue[i]);
                }
                return oldValue;
            }
        };
    }

    private static void replaceAnnotationClass(final Method m, final Annotation annotation, final Class clazz) throws NoSuchFieldException, IllegalAccessException {
        Map<Class<? extends Annotation>, Annotation> map = getMethodAnnotationsMap(m);
        map.put(clazz, annotation);
    }

    private static Map<Class<? extends Annotation>, Annotation> getMethodAnnotationsMap(final Method m) throws NoSuchFieldException, IllegalAccessException {
        Class<?> superclass = m.getClass().getSuperclass();
        java.lang.reflect.Field declaredField = superclass.getDeclaredField("declaredAnnotations");
        declaredField.setAccessible(true);
        return (Map<Class<? extends Annotation>, Annotation>) declaredField.get(m);
    }

    private static Given newGiven(final Given annotation) {
        return new Given() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return Given.class;
            }

            @Override
            public String value() {
                return substituteStepAnnotationValue(annotation.value());
            }
        };
    }

    private static Then newThen(final Then annotation) {
        return new Then() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return Then.class;
            }

            @Override
            public String value() {
                return substituteStepAnnotationValue(annotation.value());
            }
        };
    }

    private static When newWhen(final When annotation) {
        return new When() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return When.class;
            }

            @Override
            public String value() {
                return substituteStepAnnotationValue(annotation.value());
            }
        };
    }

    private static String substituteStepAnnotationValue(final String value) {
        return value.replaceAll(" ", "([\\\\s]+)");
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
