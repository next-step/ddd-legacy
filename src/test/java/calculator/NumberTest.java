package calculator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;

class NumberTest {

    @MethodSource
    @ParameterizedTest
    void constructor_with_numberString(String givenText, int expectedValue) {
        assertEquals(expectedValue, new Number(givenText).getValue());
    }

    @NullAndEmptySource
    @ParameterizedTest
    void constructor_with_nullAndEmptyString(String givenText) {
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> new Number(givenText));
    }

    public static Object[][] constructor_with_numberString() {
        return new Object[][]{
            {"10", 10},
            {"9", 9},
            {"8", 8},
            {"7", 7},
            {"6", 6},
            {"5", 5},
            {"4", 4},
            {"3", 3},
            {"2", 2},
            {"1", 1},
            {"0", 0},
            {"-1", -1},
            {"-2", -2},
            {"-3", -3},
            {"-4", -4},
            {"-5", -5},
            {"-6", -6},
            {"-7", -7},
            {"-8", -8},
            {"-9", -9},
            {"-10", -10},
        };
    }

    @Test
    void isNegative() {
        assertTrue(new Number("-1").isNegative());
        assertFalse(new Number("1").isNegative());
    }

}
