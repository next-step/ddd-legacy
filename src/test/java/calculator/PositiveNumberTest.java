package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;

class PositiveNumberTest {

    @DisplayName("0 이상의 정수로 생성할 수 있다.")
    @MethodSource
    @ParameterizedTest
    void constructor_with_numberString(String givenText, int expectedValue) {
        assertEquals(expectedValue, new PositiveNumber(givenText).getValue());
    }


    @DisplayName("null이거나 빈 문자열로 생성할 수 없다.")
    @NullAndEmptySource
    @ParameterizedTest
    void constructor_with_nullAndEmptyString(String givenText) {
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> new PositiveNumber(givenText));
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
        };
    }
    @DisplayName("음수로 생성할 수 없다.")
    @Test
    void constructor_with_negativeNumber() {
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> new PositiveNumber("-1"));
    }

}
