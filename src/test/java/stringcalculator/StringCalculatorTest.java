package stringcalculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StringCalculatorTest {

    private StringCalculator stringCalculator;

    @BeforeEach
    void setUp() {
        stringCalculator = new StringCalculator();
    }

    @DisplayName("문자열 계산기에 숫자 이외의 값 또는 음수를 전달할 수 없다.")
    @ParameterizedTest
    @ValueSource(strings = {"-1", "one"})
    @NullAndEmptySource
    void sum_invalid_negative_or_value_without_number(String value) {
        assertThatThrownBy(() -> stringCalculator.sum(value)).isInstanceOf(RuntimeException.class);
    }

    @DisplayName("쉼표(,) 구분자로 가지는 문자열을 전달하면, 구분자를 기준으로 분리한 각 숫자들의 합을 반환한다.")
    @ParameterizedTest
    @CsvSource(value = {
            "1|1",
            "1,2|3",
            "1,2,3|6"
    }, delimiter = '|')
    void sum_with_comma(String value, int expected) {
        assertThat(stringCalculator.sum(value)).isEqualTo(expected);
    }

    @DisplayName("구분자를 쉼표(,) 외로 콜론(:)도 사용이 가능하다.")
    @ParameterizedTest
    @CsvSource(value = {
            "1:2|3",
            "1,2:3|6"
    }, delimiter = '|')
    void sum_with_comma_and_colon(String value, int expected) {
        assertThat(stringCalculator.sum(value)).isEqualTo(expected);
    }
}