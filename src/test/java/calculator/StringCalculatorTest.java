package calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class StringCalculatorTest {

    private StringCalculator stringCalculator;

    @BeforeEach
    void setUp() {
        stringCalculator = new StringCalculator();
    }

    @DisplayName("빈문자열이나 null 이 들어오면 0을 리턴")
    @ParameterizedTest
    @NullAndEmptySource
    void addEmptyText(String input) {
        assertThat(stringCalculator.add(input)).isEqualTo(0);
    }

    @DisplayName("제대로 실행되는 케이스")
    @ParameterizedTest
    @ValueSource(strings = {"1:2:3", "1,2,3", "1,2:3", "//;\n1;2;3"})
    void add(String input) {
        assertThat(stringCalculator.add(input)).isEqualTo(6);
    }
}
