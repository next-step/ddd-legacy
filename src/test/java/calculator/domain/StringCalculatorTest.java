package calculator.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class StringCalculatorTest {

    private StringCalculator calculator;

    @BeforeEach
    void create() {
         this.calculator = new StringCalculator();
    }

    @DisplayName(", 또는 : 로 구분된 문자열 입력시 정상적으로 계산")
    @ParameterizedTest
    @ValueSource(strings = {"1,2,3", "1,2:3", "1:2:3"})
    void calculate(String value) {
        int result = calculator.add(value);
        assertThat(result).isEqualTo(6);
    }
}