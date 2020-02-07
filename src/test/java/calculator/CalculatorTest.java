package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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
    @ParameterizedTest()
    @ValueSource(strings = {"1,5", "1,2,3", "1,2:3"})
    void seperatorTest(String text) {
        Calculator calculator = new Calculator(text);
        assertThat(calculator.sum()).isEqualTo(6);

    }




}