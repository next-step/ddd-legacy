package stringcalculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.*;

class StringCalculatorTest {

    private StringCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new StringCalculator();
    }

    @DisplayName(value = "빈 문자열 또는 null 값을 입력할 경우 0을 반환해야 한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void zero(final String input) {
        assertThat(calculator.getSum(input)).isZero();
    }

    @Test
    @DisplayName("쉼표가 포함된 문자열인 경우 분리한 숫자 합을 반환한다.")
    void commaSum() {
        String input = "1,2,3";
        assertThat(calculator.getSum(input)).isEqualTo(6);
    }

    @Test
    @DisplayName("콜론이 포함된 문자열인 경우 분리한 숫자 합을 반환한다.")
    void colonSum() {
        String input = "1:2:3";
        assertThat(calculator.getSum(input)).isEqualTo(6);
    }

    @Test
    @DisplayName("쉼표, 콜론이 같이 있는 경우 숫자 합을 반환한다.")
    void mixSum() {
        String input = "1:2,3";
        assertThat(calculator.getSum(input)).isEqualTo(6);
    }

    @Test
    @DisplayName("커스텀 구분자로 숫자 합을 반환한다.")
    void custom() {
        String input = "//;\n1;2;3";
        assertThat(calculator.getSum(input)).isEqualTo(6);
    }

    @Test
    @DisplayName("음수 값을 넘기는 경우 RuntimeException Throw")
    void failTest1() {
        String input = "-1,3";
        assertThatRuntimeException().isThrownBy(() -> calculator.getSum(input));
    }

    @Test
    @DisplayName("(일반 구분자) 숫자 이외의 값을 넘기는 경우 RuntimeException Throw")
    void failTest2() {
        String input = "a,3";
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> calculator.getSum(input));
    }

    @Test
    @DisplayName("(커스텀 구분자) 숫자 이외의 값을 넘기는 경우 RuntimeException Throw")
    void failTest3() {
        String input = "//;\n1a;3";
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> calculator.getSum(input));
    }

    @Test
    @DisplayName(value = "숫자가 하나인 경우 해당 숫자를 반환한다.")
    void onlyNum() {
        String input = "1";
        assertThat(calculator.getSum(input)).isEqualTo(1);
    }

    @Test
    @DisplayName(value = "일반,커스텀 형태 이외의 구분자를 넣는 경우 RuntimeException Throw")
    void failTest4() {
        String input = "1&2&3";
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> calculator.getSum(input));
    }
}
