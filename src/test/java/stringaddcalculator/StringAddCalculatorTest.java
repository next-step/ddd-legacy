package stringaddcalculator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class StringAddCalculatorTest {
    private StringAddCalculator calculator;

    @BeforeEach
    void setup() {
        calculator = new StringAddCalculator();
    }

    @DisplayName(value = "빈 문자열 또는 null 값을 입력할 경우 0을 반환해야 한다.")
    @NullAndEmptySource
    @ParameterizedTest
    void emptyOrNull(final String text) {
        assertThat(calculator.add(text)).isZero();
    }
}
