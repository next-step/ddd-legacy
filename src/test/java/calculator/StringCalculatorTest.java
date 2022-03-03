package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class StringCalculatorTest {

    @DisplayName("빈 문자열을 입력했을 경우 0을 반환한다")
    @Test
    void calculateEmptyString() {
        assertThat(StringCalculator.calculate("")).isZero();
    }

    @DisplayName("쉽표(,) 또는 콜론(:)을 구분자로 사용하여 합을 구한다.")
    @ParameterizedTest
    @CsvSource(value = {"1,2=3", "1,2,3=6", "1,2:3=6"}, delimiter = '=')
    void calculate(String value, int expected) {
        assertThat(StringCalculator.calculate(value)).isEqualTo(expected);
    }

    @DisplayName("“//”와 “\\n” 사이에 위치하는 문자를 커스텀 구분자로 사용하여 합을 구한다.")
    @Test
    void calculateCustomSeparator() {
        assertThat(StringCalculator.calculate("//;\\n1;2;3")).isEqualTo(6);
    }

    @DisplayName("문자열 계산기에 음수를 전달할 수 없다.")
    @ParameterizedTest
    @ValueSource(strings = {"-1", "-1,2,3", "1,2:-3"})
    void calculateNegative(String value) {
        assertThatThrownBy(() -> StringCalculator.calculate(value))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("음수를 입력할 수 없습니다.");
    }

    @DisplayName("문자열 계산기에 숫자 이외의 값을 전달할수 없다.")
    @ParameterizedTest
    @ValueSource(strings = {"null", "일,2,3", "1,2:삼"})
    void calculateNotANumber(String value) {
        assertThatThrownBy(() -> StringCalculator.calculate(value))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("숫자가 아닌 값을 입력 할 수 없습니다.");
    }
}
