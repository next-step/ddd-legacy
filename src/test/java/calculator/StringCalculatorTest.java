package calculator;

import calculator.domain.SplitStrategy;
import calculator.domain.StringCalculator;
import calculator.domain.StringSplitStrategy;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("문자열 덧셈 계산기 테스트")
public class StringCalculatorTest {
    private SplitStrategy strategy;
    private StringCalculator calculator;

    @BeforeEach
    void setUp() {
        strategy = new StringSplitStrategy();
        calculator = new StringCalculator();
    }

    @Test
    @DisplayName("숫자 두개를 컴마(,) 구분자로 입력할 경우 두 숫자의 합을 반환한다.(예 : “1,2”)")
    void sum_splitByComma() {
        Assertions.assertThat(calculator.calculate(strategy, "1,2")).isEqualTo(3);
    }

    @Test
    @DisplayName("구분자를 컴마(,) 이외에 콜론(:)을 사용할 수 있다. (예 : “1,2:3” => 6)")
    void sum_splitByColon() {
        Assertions.assertThat(calculator.calculate(strategy, "1,2:3")).isEqualTo(6);
    }

    @Test
    @DisplayName("“//”와 “\n” 문자 사이에 커스텀 구분자를 지정할 수 있다. (예 : “//;\n1;2;3” => 6)")
    void sum_splitByCustom() {
        Assertions.assertThat(calculator.calculate(strategy, "//;\n1;2;3")).isEqualTo(6);
    }

    @Test
    @DisplayName("음수를 전달할 경우 RuntimeException 예외가 발생해야 한다. (예 : “-1,2,3”)")
    void handleNegative() {
        Assertions.assertThatThrownBy(
                () -> calculator.calculate(strategy, "-1,2,3")
        ).isInstanceOf(RuntimeException.class);
    }
}
