package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

public class CalculatorTest {

    @DisplayName("빈 문자열이나 Null을 입력 시에 0을 반환한다.")
    @ValueSource(strings = {"", "있음"})
    @NullSource
    @ParameterizedTest
    void nullCheck(String input) {
        assertThat(new Calculator().calculate(input)).isEqualTo(0);
    }

}
