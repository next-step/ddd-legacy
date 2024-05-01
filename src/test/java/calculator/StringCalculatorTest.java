package calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

public class StringCalculatorTest {

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("빈 문자열 또는 null이 들어오면 0을 반환한다")
    void null_or_empty(String input) {
        int result = StringCalculator.calculate(input);
        assertThat(result).isEqualTo(0);
    }

    @Test
    @DisplayName("숫자 하나가 들어오면 해당 숫자를 반환한다")
    void one_number() {
        int result = StringCalculator.calculate("1");
        assertThat(result).isEqualTo(1);
    }

    @ParameterizedTest
    @DisplayName("쉼표 또는 콜론을 기준으로 분리한 각 숫자의 합을 반환한다")
    @CsvSource(value = {"1,2|3", "1,2:3|6", "1,2:3,4|10"}, delimiter = '|')
    public void multiple_numbers_comma_or_colon(String input, int expected) {
        int result = StringCalculator.calculate(input);
        assertThat(result).isEqualTo(expected);
    }

    @ParameterizedTest
    @DisplayName("숫자 이외의 값 또는 음수를 전달하는 경우 RuntimeException을 throw 한다")
    @ValueSource(strings = {"-1", "a"})
    public void negative_or_not_number(String input) {
        assertThatThrownBy(() -> StringCalculator.calculate(input))
            .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("커스텀 구분자를 사용할 수 있다")
    public void custom_delimiter() {
        int result = StringCalculator.calculate("//;\n1;2;3");
        assertThat(result).isEqualTo(6);
    }
}
