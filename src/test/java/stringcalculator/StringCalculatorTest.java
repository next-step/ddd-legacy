package stringcalculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StringCalculatorTest {

    private StringCalculator stringCalculator;

    @BeforeEach
    void setUp() {
        stringCalculator = new StringCalculator();
    }

    @DisplayName("문자열 계산기에 숫자 이외의 값 또는 음수를 전달할 수 없다.")
    @ParameterizedTest
    @ValueSource(strings = {"-1", "one"})
    @NullAndEmptySource
    void sum_invalid_negative_or_value_without_number(String value) {
        assertThatThrownBy(() -> stringCalculator.sum(value)).isInstanceOf(RuntimeException.class);
    }
}