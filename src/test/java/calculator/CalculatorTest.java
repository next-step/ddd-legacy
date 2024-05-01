package calculator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class CalculatorTest {


    @DisplayName("계산기는 문자열을 입력받는다")
    @Test
    void calculatorInputString() {
        Assertions.assertThatCode(() -> new Calculator("1,2,3"))
                .doesNotThrowAnyException();
    }

    @DisplayName("계산기는 문자열과 구분자를 입력받는다")
    @Test
    void calculatorInputStringAndCustomeDelimiter() {
        Assertions.assertThatCode(() -> new Calculator("//&\n1&2&3"))
                .doesNotThrowAnyException();
    }

    @DisplayName("계산기는 입력 숫자 문자열의 합을 반환한다")
    @Test
    void calculatorReturnSum() {

        Calculator calculator = new Calculator("1,2,3");
        int result = calculator.sum();

        assertThat(result).isEqualTo(6);
    }

    @DisplayName("계산기는 숫자 이외의 문자열을 입력 받으면 예외 던진다")
    @Test
    void calculatorThrowRuntimeExceptionWhenNonNumberString() {

        assertThatThrownBy(() -> new Calculator("A,B,C"))
                .isInstanceOf(RuntimeException.class);
    }

    @DisplayName("계산기는 음수를 입력 받으면 예외 던진다")
    @Test
    void calculatorThrowRuntimeExceptionWhenNegativeNumber() {

        assertThatThrownBy(() -> new Calculator("1,2,-1"))
                .isInstanceOf(RuntimeException.class);
    }
}
