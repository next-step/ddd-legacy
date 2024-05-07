package calculator;

import calculator.exception.IllegalDelimiterArgumentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NumbersParserUtilsTest {
    @DisplayName("구분자를 포함한 문자열을 입력하면, 그 구분자로 분리하여 숫자들을 반환한다.")
    @ValueSource(strings = {"1,2,3,4,5", "1,2:3:4,5", "//;\n1;2;3;4;5", "//!\n1!2!3!4!5"})
    @ParameterizedTest
    void parseContainingDelimiter(String input) {
        // when
        Numbers actual = NumbersParserUtils.parse(input);

        // then
        assertThat(actual).isEqualTo(new Numbers("1", "2", "3", "4", "5"));
    }

    @DisplayName("입력이 'null' 이나 'empty' 일 때 0을 반환한다.")
    @NullAndEmptySource
    @ParameterizedTest
    void parseNullOrEmpty(String input) {
        // when
        Numbers actual = NumbersParserUtils.parse(input);

        // then
        assertThat(actual).isEqualTo(Numbers.ZERO_NUMBERS);
    }

    @DisplayName("유효하지 않은 구분자 형식이 입력되면 예외가 발생된다.")
    @ValueSource(strings = {"1,2:3;4", "1&2&3;4;5"})
    @ParameterizedTest
    void parseBasicStrategy(String input) {
        assertThatThrownBy(() -> NumbersParserUtils.parse(input))
                .isInstanceOf(IllegalDelimiterArgumentException.class);
    }
}
