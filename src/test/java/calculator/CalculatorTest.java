package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
    @Test
    void seperatorTest() {
        Calculator calculator1 = new Calculator("1,2");
        Calculator calculator2 = new Calculator("1,2,3");
        Calculator calculator3 = new Calculator("1,2:3");
        assertThat(calculator1.sum()).isEqualTo(3);
        assertThat(calculator2.sum()).isEqualTo(6);
        assertThat(calculator3.sum()).isEqualTo(6);

    }




}