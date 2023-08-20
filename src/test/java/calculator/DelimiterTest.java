package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.*;

class DelimiterTest {

    @DisplayName("텍스트로부터 숫자를 추출한다.")
    @MethodSource
    @ParameterizedTest
    void extractNumbers(String givenText, List<PositiveNumber> expectedResult) {
        assertEquals(expectedResult, Delimiter.extractNumbers(givenText));
    }

    public static Object[][] extractNumbers() {
        return new Object[][]{
            {
                "//;\n1",
                List.of(new PositiveNumber(1))
            },
            {
                "//;\n1;2",
                List.of(new PositiveNumber(1), new PositiveNumber(2))
            },
            {
                "//;\n1;2;3",
                List.of(new PositiveNumber(1), new PositiveNumber(2), new PositiveNumber(3))
            },
            {
                "//_\n1:2_3",
                List.of(new PositiveNumber(1), new PositiveNumber(2), new PositiveNumber(3))
            },
            {
                "//@\n1@2@3:4",
                List.of(new PositiveNumber(1), new PositiveNumber(2), new PositiveNumber(3), new PositiveNumber(4))
            },
            {
                "1",
                List.of(new PositiveNumber(1))
            },
            {
                "1,2",
                List.of(new PositiveNumber(1), new PositiveNumber(2))
            },
            {
                "1,2:3",
                List.of(new PositiveNumber(1), new PositiveNumber(2), new PositiveNumber(3))
            },
        };
    }

    @DisplayName("텍스트로부터 숫자를 추출한다.")
    @MethodSource
    @ParameterizedTest
    void extractNumbers_throw_exception_when_given_illegal_text(String givenText, String description) {
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> Delimiter.extractNumbers(givenText));
    }

    public static Object[][] extractNumbers_throw_exception_when_given_illegal_text() {
        return new Object[][]{
            {
                "//;\n",
                "커스텀 구분자만 있는 경우"
            },
            {
                "1;2;3",
                "숫자가 커스텀 구분자로 구분되어 있는 경우"
            },
            {
                "",
                "텍스트가 공백만 포함한 경우"
            },
            {
                null,
                "텍스트가 null인 경우"
            }
        };
    }

}
