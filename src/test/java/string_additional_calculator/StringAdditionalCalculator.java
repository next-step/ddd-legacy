package string_additional_calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
}
