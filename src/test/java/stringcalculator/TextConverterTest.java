package stringcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class TextConverterTest {

    @DisplayName(value = "숫자 하나를 문자열로 입력할 경우 양의 정수가 담긴 목록을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1", "2"})
    void oneNumber(final String givenText) {
        final String givenDelimiter = ",|:";
        final PositiveNumber expected = new PositiveNumber(Integer.parseInt(givenText));

        final PositiveNumbers actual = TextConverter.convertToNumbers(givenText, givenDelimiter);

        assertThat(actual.getNumbers()).containsExactly(expected);
    }

    @DisplayName("빈값을 입력하면 0인 양의 정수 목록를 리턴한다")
    @ParameterizedTest
    @NullAndEmptySource
    void null_or_empty(String text) {
        final String givenDelimiter = ",|:";

        final PositiveNumbers actual = TextConverter.convertToNumbers(text, givenDelimiter);

        assertThat(actual.getNumbers()).isEmpty();
    }

    @DisplayName(value = "숫자로된 텍스트와 구분자 쉼표 또는 콜론을 기준으로 양의 정수 목록을 리턴한다")
    @ParameterizedTest
    @ValueSource(strings = {"1,2", "1:2"})
    void delimiters(final String text) {
        final String givenDelimiter = ",|:";
        final PositiveNumber expectedNumber1 = new PositiveNumber(1);
        final PositiveNumber expectedNumber2 = new PositiveNumber(2);

        final PositiveNumbers actual = TextConverter.convertToNumbers(text, givenDelimiter);

        assertThat(actual.getNumbers()).containsExactly(expectedNumber1, expectedNumber2);
    }

    @DisplayName(value = "//와 \\n 문자 사이에 커스텀 구분자를 기준으로 양의 정수 목록을 리턴한다")
    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;2;3"})
    void custom_delimiter(final String text) {
        final String givenDelimiter = ",|:";
        final PositiveNumbers actual = TextConverter.convertToNumbers(text, givenDelimiter);
        final PositiveNumber expectedNumber1 = new PositiveNumber(1);
        final PositiveNumber expectedNumber2 = new PositiveNumber(2);
        final PositiveNumber expectedNumber3 = new PositiveNumber(3);

        assertThat(actual.getNumbers()).containsExactly(expectedNumber1, expectedNumber2, expectedNumber3);
    }
}
