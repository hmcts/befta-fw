package uk.gov.hmcts.befta.util;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.InaccessibleObjectException;
import java.util.Map;

public final class CucumberStepAnnotationUtils {

    private static Logger logger = LoggerFactory.getLogger(CucumberStepAnnotationUtils.class);

    // Hide Utility Class Constructor : Utility classes should not have a public or default constructor (squid:S1118)
    private CucumberStepAnnotationUtils() { }

    public static void injectCommonSyntacticFlexibilitiesIntoStepDefinitions(Class<?> testClass) {
        try {
            Method[] methods = testClass.getMethods();

            for (Method m : methods) {
                Annotation[] annotations = m.getDeclaredAnnotations();

                for (Annotation a : annotations) {
                    if (a instanceof Given) {
                        Given newGiven = newGiven((Given) a);
                        replaceAnnotationClass(m, newGiven, Given.class);

                    } else if (a instanceof Given.Givens) {
                        Given.Givens newGivenRepeater = newGivenRepeater((Given.Givens) a);
                        replaceAnnotationClass(m, newGivenRepeater, Given.Givens.class);

                    } else if (a instanceof When) {
                        When newWhen = newWhen((When) a);
                        replaceAnnotationClass(m, newWhen, When.class);

                    } else if (a instanceof When.Whens) {
                        When.Whens newWhenRepeater = newWhenRepeater((When.Whens) a);
                        replaceAnnotationClass(m, newWhenRepeater, When.Whens.class);

                    } else if (a instanceof Then) {
                        Then newThen = newThen((Then) a);
                        replaceAnnotationClass(m, newThen, Then.class);

                    } else if (a instanceof Then.Thens) {
                        Then.Thens newThenRepeater = newThenRepeater((Then.Thens) a);
                        replaceAnnotationClass(m, newThenRepeater, Then.Thens.class);
                    }
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Logger logger = LoggerFactory.getLogger(CucumberStepAnnotationUtils.class);
            logger.error("Error occurred when adjusting cucumber step annotations.", e);
        }
    }
    
    @SuppressWarnings("unchecked")
    private static void replaceAnnotationClass(final Method m, final Annotation annotation, final Class<? extends Annotation> clazz)
            throws NoSuchFieldException, IllegalAccessException {

        Class<?> superclass = m.getClass().getSuperclass();
        Field declaredField = superclass.getDeclaredField("declaredAnnotations");
        //Field declaredField = Unsafe.class.getDeclaredField("declaredAnnotations");
        //declaredField.setAccessible(true);

        logger.info("initial accessibility of field {} is {}.", declaredField ,declaredField.isAccessible());

        if (!declaredField.isAccessible()) {
            if (declaredField.trySetAccessible()) {
                logger.info("accessibility of field {} set to true successfully", declaredField);
            }
            else {
                logger.error("accessibility of field {} couldn't be set to true", declaredField);
            }
        }

//        try {
//            declaredField.setAccessible(true);
//        } catch (InaccessibleObjectException e) {
//            logger.error("Error occurred when annotations to accessible.", e);
//        }

//        logger.info("accessibility of field {} is {}.", declaredField ,declaredField.isAccessible());

        Map<Class<? extends Annotation>, Annotation> map = (Map<Class<? extends Annotation>, Annotation>) declaredField.get(m);
        map.put(clazz, annotation);
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

    private static Given.Givens newGivenRepeater(final Given.Givens a) {
        return new Given.Givens() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return Given.Givens.class;
            }

            @Override
            public Given[] value() {
                Given[] oldValue = a.value();
                for (int i = 0; i < oldValue.length; i++) {
                    oldValue[i] = newGiven(oldValue[i]);
                }
                return oldValue;
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

    private static Then.Thens newThenRepeater(final Then.Thens a) {
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

    private static When.Whens newWhenRepeater(final When.Whens a) {
        return new When.Whens() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return When.Whens.class;
            }

            @Override
            public When[] value() {
                When[] oldValue = a.value();
                for (int i = 0; i < oldValue.length; i++) {
                    oldValue[i] = newWhen(oldValue[i]);
                }
                return oldValue;
            }
        };
    }

    private static String substituteStepAnnotationValue(final String value) {
        // RDM-7423: Optional Comma or Full Stop At End Of DSL Elements: i.e. append optionals "(.)" & "(,)"
        // RDM-7424: Extra Space Tolerance Between Words of DSL Elements: i.e. " " => "([\s]+)"
        return value.replace(" ", "([\\s]+)") + "(.)(,)";
    }
}
