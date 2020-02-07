package uk.gov.hmcts.befta.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;


@RunWith(PowerMockRunner.class)
public class StringUtilsTest {

    @Test
    public void shouldReturnTitleCaseForUpperCaseString() {
        final String result = StringUtils.getTitleCaseFor("TITLE CASE");

        assertEquals("Title case", result);
    }

    @Test
    public void shouldHandleReturningTitleCaseForEmptyString() {
        final String result = StringUtils.getTitleCaseFor("");

        assertEquals("", result);
    }

    @Test
    public void shouldReturnFirstLetterUpperCase() {
        final String result = StringUtils.firstLetterToUpperCase("first letter");

        assertEquals("First letter", result);
    }

    @Test
    public void shouldHandleReturningFirstLetterUpperCaseForEmptyString() {
        final String result = StringUtils.firstLetterToUpperCase("");

        assertEquals("", result);
    }

    @Test
    public void shouldReturnTrueWhenComparingSameStringWithDifferentCase() {
        final boolean result = StringUtils.equalsIgnoreCase("aBc DeF", "AbC DEf");

        assertTrue(result);
    }

    @Test
    public void shouldReturnFalseWhenComparingANullString() {
        final boolean result = StringUtils.equalsIgnoreCase("Test String", null);

        assertFalse(result);
    }

    @Test
    public void shouldExtractNumericStringWithPlusFromStringWithSpecialCharacters() {
        final String result = StringUtils.extractNumericStringWithPlus("1 +'\"    -_.()23");

        assertEquals("+123", result);
    }

    @Test
    public void shouldExtractNumericStringWithoutPlusFromStringWithSpecialCharacters() {
        final String result = StringUtils.extractNumericStringWithoutPlus("1 +'\"    -_.()23");

        assertEquals("123", result);
    }

}
