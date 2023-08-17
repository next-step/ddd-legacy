package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CalculatorTest {

    @DisplayName("빈 문자 열의 경우 0을 반환")
    @Test
    void emptyValueTest() {
        Calculator calculator = new Calculator("");

        assertThat(calculator.calculate()).isEqualTo(0);
    }

    @DisplayName("숫자 한 자리도 정상 처리 한다.")
    @Test
    void singleValueTest() {
        Calculator calculator = new Calculator("5");

        assertThat(calculator.calculate()).isEqualTo(5);
    }

    @DisplayName("쉼표 구분 자 > 1,2 -> 3")
    @Test
    void addDelimitersTest1() {
        Calculator calculator = new Calculator("11:23");

        assertThat(calculator.calculate()).isEqualTo(34);
    }

    @DisplayName("콜론 구분 자 > 3:4 -> 7")
    @Test
    void addDelimitersTest2() {
        Calculator calculator = new Calculator("3:4");

        assertThat(calculator.calculate()).isEqualTo(7);
    }

    @DisplayName("두 자리, 세 자리 값 > 11:23:134 -> 168.")
    @Test
    void addBigNum() {
        Calculator calculator = new Calculator("11:23:134");

        assertThat(calculator.calculate()).isEqualTo(168);
    }

    @DisplayName("정해진 구분자 혹은 숫자가 아니면 Runtime 예외 발생")
    @Test
    void runtimeExceptionTest1() {
        assertThatThrownBy(() -> new Calculator("11!23")).isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> new Calculator("안녕,23")).isInstanceOf(RuntimeException.class);
    }

    @DisplayName("음수의 경우에 Runtime 예외 발생")
    @Test
    void runtimeExceptionTest2() {
        assertThatThrownBy(() -> new Calculator("-1,23")).isInstanceOf(RuntimeException.class);
    }

    @DisplayName(value = "//와 \\n 문자 사이에 커스텀 구분 자를 지정할 수 있다.")
    @Test
    void customDelimiter() {
        Calculator calculator = new Calculator("//!\n131!313");

        assertThat(calculator.calculate()).isEqualTo(444);
    }
}

