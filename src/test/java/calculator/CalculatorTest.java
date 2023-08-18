package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CalculatorTest {

    @DisplayName("빈 문자 열의 경우 0을 반환")
    @Test
    void emptyValueTest() {
        Calculator calculator = new Calculator("");

        assertThat(calculator.calculate()).isEqualTo(0);
    }

    @DisplayName("계산기 기본 동작 테스트")
    @ParameterizedTest
    @CsvSource(value = {"5=5","3:4=7", "11:23=34", "11:23,134=168"}, delimiter = '=')
    void singleValueTest(String input, String expect) {
        Calculator calculator = new Calculator(input);

        assertThat(calculator.calculate()).isEqualTo(Integer.parseInt(expect));
    }

    @DisplayName("계산기에 정해진 값 이외의 값을 입력하면 Runtime 에러가 발생한다")
    @ParameterizedTest
    @CsvSource(value = {"11!23","안녕,23", "-1,23"})
    void runtimeExceptionTest1(String input) {
        assertThatThrownBy(() -> new Calculator(input)).isInstanceOf(RuntimeException.class);
    }

    @DisplayName(value = "//와 \\n 문자 사이에 커스텀 구분 자를 지정할 수 있다.")
    @ParameterizedTest
    @CsvSource(value = {"'//!\n131!313'=444","'//-\n'1-2=3", "'//A\n'2A2=4"}, delimiter = '=')
    void customDelimiter(String input, String expect) {
        input = input.replaceAll("'", "");
        Calculator calculator = new Calculator(input);

        assertThat(calculator.calculate()).isEqualTo(Integer.parseInt(expect));
    }
}

