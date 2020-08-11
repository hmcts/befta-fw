package uk.gov.hmcts.common;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class TestUtils {

    public static void setFieldWithReflection(Field field, Object newValue) throws Exception {
        setFieldWithReflection(null, field, newValue);
    }

    public static void setFieldWithReflection(Object object, Field field, Object newValue) throws Exception {
        field.setAccessible(true);
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(object, newValue);
    }

}
