package calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

public class CalculatorTest {

    private Calculator calculator;

    // 전체 생성하는거 추가
    @BeforeEach
    void setCalculator() {
        calculator = new Calculator();
    }

    @DisplayName("빈 문자열이나 Null을 입력 시에 0을 반환한다.")
    @NullAndEmptySource
    @ParameterizedTest
    void nullCheck(String input) {
        assertThat(calculator.calculate(input)).isEqualTo(0);
    }

    @DisplayName("문자열이 숫자하나만 들어왔을 경우 숫자를 반환한다.")
    @ValueSource(strings = {"1", "22", "333", "1a", "aa"})
    @ParameterizedTest
    void onlyNumberCheck(String input) {
        assertThat(calculator.calculate(input)).isEqualTo(Integer.parseInt(input));
    }

    @DisplayName("숫자 두개를 컴마 구분자자의 합을 반환한다.")
    @ValueSource(strings = {"1,1", "2,2", "3,33", "1,a", "1,,1"})
    @ParameterizedTest
    void separateCommaCheck(String input) {
        assertThat(calculator.calculate(input)).isEqualTo(Integer.parseInt(input));
    }
}
