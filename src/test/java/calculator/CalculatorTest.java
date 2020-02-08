package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class CalculatorTest {

    @DisplayName("null 을 입력할 경우 0을 반환해야 한다.")
    @Test
    void nullTest() {
        Calculator calculator = new Calculator(null);
        assertThat(calculator.sum()).isEqualTo(0);
    }

    @DisplayName("빈 텍스트를 입력할 경우 0을 반환해야 한다.")
    @Test
    void emptyTest() {
        Calculator calculator = new Calculator("");
        assertThat(calculator.sum()).isEqualTo(0);
    }

    @DisplayName("\",\" 또는 \":\" 을 구분자로 가지는 문자열을 전달할 경우 구분자를 기준으로 분리한 각 숫자의 합을 반환해야 한다." )
    @ParameterizedTest()
    @ValueSource(strings = {"1,5", "1,2,3", "1,2:3"})
    void seperatorTest(String text) {
        Calculator calculator = new Calculator(text);
        assertThat(calculator.sum()).isEqualTo(6);

    }

    @DisplayName("문자열 계산기에 숫자 이외의 값 또는 음수를 전달하는 경우 RuntimeException 예외를 throw 한다.")
    @Test
    void minusNumberTest() {
        Calculator calculator = new Calculator("-1,2,3");
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() ->{
                    calculator.sum();
                });
    }

    @DisplayName("\"//\"와 \"\\n\" 문자 사이에 커스텀 구분자를 지정할 수 있다.")
    @Test
    void customSeperatorTest() {
        Calculator calculator = new Calculator("//;\n1;2;3");
        assertThat(calculator.sum()).isEqualTo(6);
    }




}