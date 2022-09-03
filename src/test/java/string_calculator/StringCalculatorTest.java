package string_calculator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import string_calculator.string_parser.SimpleStringParser;

class StringCalculatorTest {

    private final SimpleStringParser simpleStringParser = new SimpleStringParser();

    private final StringCalculator stringCalculator = new StringCalculator(simpleStringParser);

    @DisplayName("string이 null 또는 empty인 경우 0을 반환해야 한다.")
    @NullAndEmptySource
    @ParameterizedTest
    void string_null_or_empty(final String string) {
        final Long result = this.stringCalculator.calculate(string);
        assertThat(result).isZero();
    }

    @DisplayName("음수가 아닌 정수 하나가 입력된 경우 같은 값을 반환해야 한다.")
    @ValueSource(strings = {"1"})
    @ParameterizedTest
    void string_single_non_negative_integer(final String string) {
        final Long result = this.stringCalculator.calculate(string);
        assertThat(result).isEqualTo(Long.parseLong(string));
    }

    @DisplayName("여러 정수가 입력된 경우 숫자들의 합을 반환해야 한다.")
    @ValueSource(strings = {
            "1,2,3,4,5",
            "1:2:3:4:5",
            "1,2:3,4:5",
    })
    @ParameterizedTest
    void string_multiple_integers(final String string) {
        final Long result = this.stringCalculator.calculate(string);
        assertThat(result).isEqualTo(15L);
    }
}
