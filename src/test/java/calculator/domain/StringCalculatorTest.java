package calculator.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    @DisplayName("커스텀 구분자 테스트")
    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;2;3", "//!\n1!2!3", "//@\n1@2@3"})
    void customDelimiter(String value) {
        int result = calculator.add(value);
        assertThat(result).isEqualTo(6);
    }

    @ParameterizedTest
    @DisplayName("빈문자열 또는 Null 입력시 0 리턴")
    @NullAndEmptySource
    void emptyAndNullInput(String value) {
        int result = calculator.add(value);
        assertThat(result).isEqualTo(0);
    }

    @ParameterizedTest
    @DisplayName("음수 또는 문자열 입력시 RuntimeException")
    @ValueSource(strings = {"1:-2:3", "a:b:c", "1:b:-3"})
    void calculateFail(String value) {
        assertThrows(RuntimeException.class, () -> calculator.add(value));
    }
}