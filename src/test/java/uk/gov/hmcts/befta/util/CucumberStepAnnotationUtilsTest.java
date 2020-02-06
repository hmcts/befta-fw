package uk.gov.hmcts.befta.util;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

public class CucumberStepAnnotationUtilsTest {

    private static final String GIVEN = "GIVEN";
    private static final String THEN = "THEN";
    private static final String WHEN = "WHEN";

    private static final String SINGLE_TEST_ANNOTATION = "single test annotation";
    private static final String SINGLE_TEST_ANNOTATION_RESULT = "single([\\s]+)test([\\s]+)annotation";

    private static final String MULTIPLE_TEST_ANNOTATION_1 = "multiple test annotation 1";
    private static final String MULTIPLE_TEST_ANNOTATION_1_RESULT = "multiple([\\s]+)test([\\s]+)annotation([\\s]+)1";

    private static final String MULTIPLE_TEST_ANNOTATION_2 = "multiple test annotation 2";
    private static final String MULTIPLE_TEST_ANNOTATION_2_RESULT = "multiple([\\s]+)test([\\s]+)annotation([\\s]+)2";

    @Test
    public void shouldAdjustSingleGivenCucumberStepAnnotation() {
        // ARRANGE
        class SingleGiven {

            @Given(SINGLE_TEST_ANNOTATION)
            public SingleGiven singleGivenAnnotation() {
                return this;
            }

        }

        // ACT
        CucumberStepAnnotationUtils.adjustCucumberStepAnnotations(SingleGiven.class);

        // ASSERT
        Map<String, Map<String, List<String>>> annotationInfo = readAnnotations(SingleGiven.class);
        Assert.assertTrue(annotationInfo.containsKey("singleGivenAnnotation"));

        Map<String, List<String>> methodAnnotationInfo = annotationInfo.get("singleGivenAnnotation");
        Assert.assertTrue(methodAnnotationInfo.containsKey(GIVEN));
        Assert.assertEquals(Collections.singletonList(SINGLE_TEST_ANNOTATION_RESULT), methodAnnotationInfo.get(GIVEN));

        // 100% test coverage: create class and call function
        (new SingleGiven()).singleGivenAnnotation();
    }

    @Test
    public void shouldAdjustSingleThenCucumberStepAnnotation() {
        // ARRANGE
        class SingleThen {

            @Then(SINGLE_TEST_ANNOTATION)
            public SingleThen singleThenAnnotation() {
                return this;
            }

        }

        // ACT
        CucumberStepAnnotationUtils.adjustCucumberStepAnnotations(SingleThen.class);

        // ASSERT
        Map<String, Map<String, List<String>>> annotationInfo = readAnnotations(SingleThen.class);
        Assert.assertTrue(annotationInfo.containsKey("singleThenAnnotation"));

        Map<String, List<String>> methodAnnotationInfo = annotationInfo.get("singleThenAnnotation");
        Assert.assertTrue(methodAnnotationInfo.containsKey(THEN));
        Assert.assertEquals(Collections.singletonList(SINGLE_TEST_ANNOTATION_RESULT), methodAnnotationInfo.get(THEN));

        // 100% test coverage: create class and call function
        (new SingleThen()).singleThenAnnotation();
    }

    @Test
    public void shouldAdjustSingleWhenCucumberStepAnnotation() {
        // ARRANGE
        class SingleWhen {

            @When(SINGLE_TEST_ANNOTATION)
            public SingleWhen singleWhenAnnotation() {
                return this;
            }

        }

        // ACT
        CucumberStepAnnotationUtils.adjustCucumberStepAnnotations(SingleWhen.class);

        // ASSERT
        Map<String, Map<String, List<String>>> annotationInfo = readAnnotations(SingleWhen.class);
        Assert.assertTrue(annotationInfo.containsKey("singleWhenAnnotation"));

        Map<String, List<String>> methodAnnotationInfo = annotationInfo.get("singleWhenAnnotation");
        Assert.assertTrue(methodAnnotationInfo.containsKey(WHEN));
        Assert.assertEquals(Collections.singletonList(SINGLE_TEST_ANNOTATION_RESULT), methodAnnotationInfo.get(WHEN));

        // 100% test coverage: create class and call function
        (new SingleWhen()).singleWhenAnnotation();
    }

    @Test
    public void shouldAdjustSingleMixedCucumberStepAnnotations() {
        // ARRANGE
        class SingleMixed {

            @Given(SINGLE_TEST_ANNOTATION)
            @When(SINGLE_TEST_ANNOTATION)
            public SingleMixed singleGivenWhenAnnotation() {
                return this;
            }

            @When(SINGLE_TEST_ANNOTATION)
            @Then(SINGLE_TEST_ANNOTATION)
            public SingleMixed singleWhenThenAnnotation() {
                return this;
            }

            @Given(SINGLE_TEST_ANNOTATION)
            @When(SINGLE_TEST_ANNOTATION)
            @Then(SINGLE_TEST_ANNOTATION)
            public SingleMixed singleAllAnnotation() {
                return this;
            }

        }

        // ACT
        CucumberStepAnnotationUtils.adjustCucumberStepAnnotations(SingleMixed.class);

        // ASSERT
        Map<String, Map<String, List<String>>> annotationInfo = readAnnotations(SingleMixed.class);
        Assert.assertTrue(annotationInfo.containsKey("singleGivenWhenAnnotation"));
        Assert.assertTrue(annotationInfo.containsKey("singleWhenThenAnnotation"));
        Assert.assertTrue(annotationInfo.containsKey("singleAllAnnotation"));

        Map<String, List<String>> methodAnnotationInfo = annotationInfo.get("singleGivenWhenAnnotation");
        Assert.assertTrue(methodAnnotationInfo.containsKey(GIVEN));
        Assert.assertTrue(methodAnnotationInfo.containsKey(WHEN));
        Assert.assertEquals(Collections.singletonList(SINGLE_TEST_ANNOTATION_RESULT), methodAnnotationInfo.get(GIVEN));
        Assert.assertEquals(Collections.singletonList(SINGLE_TEST_ANNOTATION_RESULT), methodAnnotationInfo.get(WHEN));

        methodAnnotationInfo = annotationInfo.get("singleWhenThenAnnotation");
        Assert.assertTrue(methodAnnotationInfo.containsKey(WHEN));
        Assert.assertTrue(methodAnnotationInfo.containsKey(THEN));
        Assert.assertEquals(Collections.singletonList(SINGLE_TEST_ANNOTATION_RESULT), methodAnnotationInfo.get(WHEN));
        Assert.assertEquals(Collections.singletonList(SINGLE_TEST_ANNOTATION_RESULT), methodAnnotationInfo.get(THEN));

        methodAnnotationInfo = annotationInfo.get("singleAllAnnotation");
        Assert.assertTrue(methodAnnotationInfo.containsKey(GIVEN));
        Assert.assertTrue(methodAnnotationInfo.containsKey(WHEN));
        Assert.assertTrue(methodAnnotationInfo.containsKey(THEN));
        Assert.assertEquals(Collections.singletonList(SINGLE_TEST_ANNOTATION_RESULT), methodAnnotationInfo.get(GIVEN));
        Assert.assertEquals(Collections.singletonList(SINGLE_TEST_ANNOTATION_RESULT), methodAnnotationInfo.get(WHEN));
        Assert.assertEquals(Collections.singletonList(SINGLE_TEST_ANNOTATION_RESULT), methodAnnotationInfo.get(THEN));

        // 100% test coverage: create class and call function
        (new SingleMixed()).singleGivenWhenAnnotation().singleWhenThenAnnotation().singleAllAnnotation();
    }

    @Test
    public void shouldAdjustMultipleGivenCucumberStepAnnotations() {
        // ARRANGE
        class MultipleThen {

            @Given(MULTIPLE_TEST_ANNOTATION_1)
            @Given(MULTIPLE_TEST_ANNOTATION_2)
            public MultipleThen multipleGivenAnnotation() {
                return this;
            }

        }

        // ACT
        CucumberStepAnnotationUtils.adjustCucumberStepAnnotations(MultipleThen.class);

        // ASSERT
        Map<String, Map<String, List<String>>> annotationInfo = readAnnotations(MultipleThen.class);
        Assert.assertTrue(annotationInfo.containsKey("multipleGivenAnnotation"));

        Map<String, List<String>> methodAnnotationInfo = annotationInfo.get("multipleGivenAnnotation");
        Assert.assertTrue(methodAnnotationInfo.containsKey(GIVEN));
        Assert.assertEquals(Arrays.asList(MULTIPLE_TEST_ANNOTATION_1_RESULT, MULTIPLE_TEST_ANNOTATION_2_RESULT), methodAnnotationInfo.get(GIVEN));

        // 100% test coverage: create class and call function
        (new MultipleThen()).multipleGivenAnnotation();
    }

    @Test
    public void shouldAdjustMultipleThenCucumberStepAnnotations() {
        // ARRANGE
        class MultipleThen {

            @Then(MULTIPLE_TEST_ANNOTATION_1)
            @Then(MULTIPLE_TEST_ANNOTATION_2)
            public MultipleThen multipleThenAnnotation() {
                return this;
            }

        }

        // ACT
        CucumberStepAnnotationUtils.adjustCucumberStepAnnotations(MultipleThen.class);

        // ASSERT
        Map<String, Map<String, List<String>>> annotationInfo = readAnnotations(MultipleThen.class);
        Assert.assertTrue(annotationInfo.containsKey("multipleThenAnnotation"));

        Map<String, List<String>> methodAnnotationInfo = annotationInfo.get("multipleThenAnnotation");
        Assert.assertTrue(methodAnnotationInfo.containsKey(THEN));
        Assert.assertEquals(Arrays.asList(MULTIPLE_TEST_ANNOTATION_1_RESULT, MULTIPLE_TEST_ANNOTATION_2_RESULT), methodAnnotationInfo.get(THEN));

        // 100% test coverage: create class and call function
        (new MultipleThen()).multipleThenAnnotation();
    }

    @Test
    public void shouldAdjustMultipleWhensCucumberStepAnnotations() {
        // ARRANGE
        class MultipleThen {

            @When(MULTIPLE_TEST_ANNOTATION_1)
            @When(MULTIPLE_TEST_ANNOTATION_2)
            public MultipleThen multipleWhenAnnotation() {
                return this;
            }

        }

        // ACT
        CucumberStepAnnotationUtils.adjustCucumberStepAnnotations(MultipleThen.class);

        // ASSERT
        Map<String, Map<String, List<String>>> annotationInfo = readAnnotations(MultipleThen.class);
        Assert.assertTrue(annotationInfo.containsKey("multipleWhenAnnotation"));

        Map<String, List<String>> methodAnnotationInfo = annotationInfo.get("multipleWhenAnnotation");
        Assert.assertTrue(methodAnnotationInfo.containsKey(WHEN));
        Assert.assertEquals(Arrays.asList(MULTIPLE_TEST_ANNOTATION_1_RESULT, MULTIPLE_TEST_ANNOTATION_2_RESULT), methodAnnotationInfo.get(WHEN));

        // 100% test coverage: create class and call function
        (new MultipleThen()).multipleWhenAnnotation();
    }

    @Test
    public void shouldNotErrorWhenNoCucumberStepAnnotation() {
        // ARRANGE
        class NoStepAnnotations {

            // NB: needs an annotation so re-use @Test as it is close at hand
            @Test
            public NoStepAnnotations notAStepAnnotation() {
                return this;
            }

            public NoStepAnnotations noAnnotations() {
                return this;
            }

        }

        // ACT
        CucumberStepAnnotationUtils.adjustCucumberStepAnnotations(NoStepAnnotations.class);

        // ASSERT
        Map<String, Map<String, List<String>>> annotationInfo = readAnnotations(NoStepAnnotations.class);
        Assert.assertTrue(annotationInfo.containsKey("notAStepAnnotation"));
        Assert.assertTrue(annotationInfo.containsKey("noAnnotations"));

        Map<String, List<String>> methodAnnotationInfo = annotationInfo.get("notAStepAnnotation");
        Assert.assertTrue(methodAnnotationInfo.isEmpty());

        methodAnnotationInfo = annotationInfo.get("noAnnotations");
        Assert.assertTrue(methodAnnotationInfo.isEmpty());

        // 100% test coverage: create class and call function
        (new NoStepAnnotations()).notAStepAnnotation().noAnnotations();
    }

    private Map<String, Map<String, List<String>>> readAnnotations(Class<?> testClass) {
        Map<String, Map<String, List<String>>> classAnnotationInfo = new HashMap<>();
        Method[] methods = testClass.getMethods();

        for (Method m : methods) {
            Map<String, List<String>> methodAnnotationInfo = new HashMap<>();
            List<String> givenList = new ArrayList<>();
            List<String> thenList = new ArrayList<>();
            List<String> whenList = new ArrayList<>();
            Annotation[] annotations = m.getDeclaredAnnotations();

            for (Annotation a : annotations) {
                if (a instanceof Given) {
                    Assert.assertEquals(Given.class, a.annotationType());
                    givenList.add(((Given)a).value());

                } else if (a instanceof Given.Givens) {
                    Assert.assertEquals(Given.Givens.class, a.annotationType());
                    Given[] givens = ((Given.Givens)a).value();
                    for (Given g : givens) {
                        givenList.add((g).value());
                    }

                } else if (a instanceof Then) {
                    Assert.assertEquals(Then.class, a.annotationType());
                    thenList.add(((Then)a).value());

                } else if (a instanceof Then.Thens) {
                    Assert.assertEquals(Then.Thens.class, a.annotationType());
                    Then[] thens = ((Then.Thens)a).value();
                    for (Then t : thens) {
                        thenList.add((t).value());
                    }

                } else if (a instanceof When) {
                    Assert.assertEquals(When.class, a.annotationType());
                    whenList.add(((When)a).value());

                } else if (a instanceof When.Whens) {
                    Assert.assertEquals(When.Whens.class, a.annotationType());
                    When[] whens = ((When.Whens)a).value();
                    for (When g : whens) {
                        whenList.add((g).value());
                    }
                }
            }
            
            if (!givenList.isEmpty()) {
                methodAnnotationInfo.put(GIVEN, givenList);
            }

            if (!thenList.isEmpty()) {
                methodAnnotationInfo.put(THEN, thenList);
            }

            if (!whenList.isEmpty()) {
                methodAnnotationInfo.put(WHEN, whenList);
            }

            classAnnotationInfo.put(m.getName(), methodAnnotationInfo);
        }

        return classAnnotationInfo;
    }
}
