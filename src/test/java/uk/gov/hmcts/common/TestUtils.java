package uk.gov.hmcts.common;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class TestUtils {

    public static void setFieldWithReflection(Field field, Object newValue) throws Exception {
        setFieldWithReflection(null, field, newValue);
    }

    public static void setFieldWithReflection(Object object, Field field, Object newValue) throws Exception {
        field.setAccessible(true);
        //Field modifiersField = Field.class.getDeclaredField("modifiers");
        Field modifiersField = getModifiersField();
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(object, newValue);
    }

    //https://github.com/prestodb/presto/pull/15240/files
    private static Field getModifiersField() throws NoSuchFieldException
    {
        try {
            return Field.class.getDeclaredField("modifiers");
        }
        catch (NoSuchFieldException e) {
            try {
                Method getDeclaredFields0 = Class.class.getDeclaredMethod("getDeclaredFields0", boolean.class);
                getDeclaredFields0.setAccessible(true);
                Field[] fields = (Field[]) getDeclaredFields0.invoke(Field.class, false);
                for (Field field : fields) {
                    if ("modifiers".equals(field.getName())) {
                        return field;
                    }
                }
            }
            catch (ReflectiveOperationException ex) {
                e.addSuppressed(ex);
            }
            throw e;
        }
    }

}
