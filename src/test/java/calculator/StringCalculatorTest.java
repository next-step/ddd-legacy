package calculator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

@DisplayName("문자열 덧셈 계산기 테스트")
public class StringCalculatorTest {
    private StringCalculator calculator;
    private SplitStrategy strategy = new StringSplitStrategy();

    @BeforeEach
    void setUp() {
        calculator = new StringCalculator();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("빈 문자열 또는 null 값을 입력할 경우 0을 반환해야 한다.(예 : “” => 0, null => 0)")
    void checkEmptyAndNull(String input) {
        Assertions.assertThat(calculator.calculate(strategy, input)).isEqualTo(0);
    }

    @Test
    @DisplayName("숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환한다.(예 : “1”)")
    void inputOnlyOne() {
        Assertions.assertThat(calculator.calculate(strategy, "1")).isEqualTo(1);
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