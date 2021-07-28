package calculator.test;

import calculator.StringCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        assertThat(calculator.add(text)).isEqualTo(expected);
    }

    @DisplayName("null 또는 빈 문자열 입력시 0 반환")
    @ParameterizedTest
    @NullAndEmptySource
    void add_nullOrEmpty(String text) {
        assertThat(calculator.add(text)).isEqualTo(0);
    }

    @DisplayName("커스텀 구분자로 분리한 숫자의 합을 반환")
    @Test
    void add_customSeparator() {
        String text = "//;\n1;2;3";
        assertThat(calculator.add(text)).isEqualTo(6);
    }

    @DisplayName("숫자가 아니거나 음수일때 오류 발생")
    @ParameterizedTest
    @CsvSource(value = {"-1:2", "a:3"})
    void add_isNotNumberAndIsNotAmniotic(String text) {
        assertThatThrownBy(() -> calculator.add(text))
                .isInstanceOf(RuntimeException.class);
    }
}
