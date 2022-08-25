package stringcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StringCalculatorTest {

    @ParameterizedTest(name = "{0} 을 전달하면 0이 반환된다.")
    @NullAndEmptySource
    void null_and_empty_string(String source) {
        assertThat(StringCalculator.calculate(source)).isZero();
    }

    @DisplayName("숫자 하나를 전달하면 숫자가 그대로 반환된다.")
    @Test
    void single_number() {
        assertThat(StringCalculator.calculate("1")).isEqualTo(1);
    }

    @DisplayName("숫자 여러 개를 쉼표(,)나 콜론(:)으로 구분해서 전달하면 숫자가 더해져서 반환된다.")
    @Test
    void multiple_numbers() {
        assertThat(StringCalculator.calculate("1,2:3,4")).isEqualTo(10);
    }

    @DisplayName("숫자가 아닌 문자를 전달하면 예외가 발생한다.")
    @Test
    void not_number() {
        assertThatThrownBy(() -> StringCalculator.calculate("1,@"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("숫자가 아니면 계산할 수 없습니다.");
    }

    @DisplayName(value = "//와 \\n 문자 사이에 커스텀 구분자를 지정할 수 있다.")
    @Test
    void custom_delimiter() {
        assertThat(StringCalculator.calculate("//;\n1;2;3")).isEqualTo(6);
    }
}
