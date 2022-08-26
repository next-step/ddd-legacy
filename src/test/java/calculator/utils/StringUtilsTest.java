package calculator.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class StringUtilsTest {

    @DisplayName("null 일 경우 빈 문자열을 반환한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void defaultString(String actual) {
        assertThat(StringUtils.defaultString(actual))
                .isBlank();
    }

    @DisplayName("빈 문자열 또는 null 일 경우 true 반환")
    @ParameterizedTest
    @NullAndEmptySource
    void isBlank(String actual) {
        assertThat(StringUtils.isBlank(actual))
                .isTrue();
    }

    @DisplayName("toList")
    @Nested
    class ToList {

        @DisplayName("문자열과 구분자를 주입받아 리스트로 반환한다.")
        @ParameterizedTest
        @CsvSource(value = {",=0", ",,=0", ",1,=2", "1=1", "1,2,3=3"},
                delimiter = '=')
        void toList(String actual, int expected) {
            assertThat(StringUtils.toList(actual, ","))
                    .hasSize(expected);
        }

        @DisplayName("null 또는 빈 문자열일 경우 빈 ArrayList를 반환한다.")
        @ParameterizedTest
        @NullAndEmptySource
        void nullAndEmpty(String actual) {
            assertThat(StringUtils.toList(actual, ","))
                    .isEmpty();
        }
    }
}
