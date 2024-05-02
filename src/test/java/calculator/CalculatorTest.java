package calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;


class CalculatorTest {

    Calculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }

    @ParameterizedTest
    @DisplayName("빈 문자열 또는 null을 입력할 경우 0을 반환한다.")
    @NullAndEmptySource
    void calculatorTest(String text) {
        assertThat(calculator.add(text)).isZero();
    }

}
