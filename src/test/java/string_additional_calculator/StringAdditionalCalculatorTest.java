package string_additional_calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("StringAdditionalCalculator 클래스")
class StringAdditionalCalculatorTest {

    private final StringAdditionalCalculator stringAdditionalCalculator = new StringAdditionalCalculator();

    @DisplayName("쉼표(,) 또는 콜론(:)을 구분자로 가지는 문자열을 전달하는 경우 구분자를 기준으로 분리한 각 숫자의 합을 반환한다.")
    @Test
    void calculate() {
        // given
        final String expression = "1,2:3";

        // when
        int result = stringAdditionalCalculator.calculate(expression);

        // then
        assertThat(result).isEqualTo(6);
    }

    @DisplayName("기본 구분자(쉼표, 콜론) 외에 커스텀 구분자를 지정할 수 있다.")
    @Test
    void customSeparate() {
        // given
        final String expression = "//;\\n1;2;3";

        // when
        int result = stringAdditionalCalculator.calculate(expression);

        // then
        assertThat(result).isEqualTo(6);
    }

    @DisplayName("커스텀 구분자는 한 글자만 가능하다")
    @Test
    void invalidCustomSeparate() {
        // given
        final String expression = "//;e\\n1;e2;e3";

        // when then
        assertThatThrownBy(() -> stringAdditionalCalculator.calculate(expression))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("커스텀 구분자는 1글자여야 합니다. expression: //;e\\n1;e2;e3, separator: ;e");
    }
}
