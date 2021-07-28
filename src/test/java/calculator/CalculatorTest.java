package calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class CalculatorTest {
    StringCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new StringCalculator();
    }

    @DisplayName("구분자를 기준으로 분리한 숫자의 합을 변환")
    @ParameterizedTest
    @CsvSource(value = {"1&1", "&0", "1,2&3", "1,2,3&6", "1,2:3&6"}, delimiter = '&')
    void add(String text, int expected) {
        int result = calculator.add(text);
        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("null 또는 빈 문자열 입력시 0 반환")
    @ParameterizedTest
    @NullAndEmptySource
    void add_nullOrEmpty(String text) {
        int result = calculator.add(text);
        assertThat(result).isEqualTo(0);
    }
}
