package calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class CalculatorTest {

    private StringCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new StringCalculator();
    }

    @DisplayName("콤마가 포함된 문자열을 계산한다")
    @ParameterizedTest
    @CsvSource({
        "'1,2',3",
        "'1,2,3',6",
    })
    void comma(String text, int answer) {
        assertThat(calculator.add(text)).isEqualTo(answer);
    }

    @DisplayName("콜론이 포함된 문자열을 계산할 수 있다.")
    @ParameterizedTest
    @CsvSource({
        "'1:2',3",
        "'1:2:3',6",
    })
    void colon(String text, int answer) {
        assertThat(calculator.add(text)).isEqualTo(answer);
    }

    @DisplayName(value = "콤마와 콜론이 포함된 문자열을 계산할 수 있다.")
    @ParameterizedTest
    @CsvSource({
        "'1,2:3',6",
        "'1:2,3',6",
        "'1,2:3,4',10",
        "'1:2,3:4',10",
    })
    void colonsAndColons(final String text, final int answer) {
        assertThat(calculator.add(text)).isEqualTo(answer);
    }

    @DisplayName(value = "//와 \\n 문자 사이에 커스텀 구분자를 지정할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;2;3"})
    void customDelimiter(final String text) {
        assertThat(calculator.add(text)).isSameAs(6);
    }

    @DisplayName(value = "콤마, 콜론과 커스텀 구분자가 포함된 문자열을 계산할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"//;\n1,2:3;4"})
    void commaAndSemicolonAndCustomDelimiter(final String text) {
        assertThat(calculator.add(text)).isSameAs(10);
    }

    @DisplayName(value = "숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1"})
    void oneNumber(final String text) {
        assertThat(calculator.add(text)).isSameAs(Integer.parseInt(text));
    }

    @DisplayName(value = "빈 문자열 또는 null 값을 입력할 경우 0을 반환해야 한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void emptyOrNull(final String text) {
        assertThat(calculator.add(text)).isZero();
    }

    @DisplayName(value = "문자열 계산기에 음수를 전달하는 경우 RuntimeException 예외 처리를 한다.")
    @Test
    void negative() {
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> calculator.add("-1"));
    }

    @DisplayName(value = "문자열 계산기에 숫자가 아닌 값을 전달하는 경우 RuntimeException 예외 처리를 한다.")
    @Test
    void notNumber() {
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> calculator.add("notNumber"));
    }
}
