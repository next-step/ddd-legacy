package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.junit.jupiter.api.Assertions.*;

class DelimiterTest {

    @DisplayName("구분자는 null과 빈 문자열로 생성시 기본 구분자를 가진다.")
    @NullAndEmptySource
    @ParameterizedTest
    void constructor_with_null_and_empty(String givenText) {
        assertEquals(Delimiter.DEFAULT_DELIMITER, new Delimiter(givenText).getValue());
    }

    @DisplayName("구분자는 text에 따라 구분자가 정해진다.")
    @MethodSource
    @ParameterizedTest
    void constructor_with_text(String givenText, String expectedValue) {
        assertEquals(expectedValue, new Delimiter(givenText).getValue());
    }

    public static Object[][] constructor_with_text() {
        return new Object[][]{
            {"//;\n", ",|:|;"},
            {"//;\n1", ",|:|;"},
            {"//;\n1;2", ",|:|;"},
            {"//;\n1;2;3", ",|:|;"},
            {"//_\n1;2;3", ",|:|_"},
            {"//@\n1;2;3", ",|:|@"},
            {"1", ",|:"},
            {"1,2", ",|:"},
            {"1,2:3", ",|:"},
        };
    }

}
