package stringcalculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class StringCalculatorTest {
    private StringCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new StringCalculator();
    }

    @DisplayName(value = "빈 문자열 또는 null 값을 입력할 경우 0을 반환해야 한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void emptyOrNull(final String text) {
        assertThat(calculator.add(text)).isZero();
    }

    @DisplayName(value = "숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1", "100", "0"})
    void oneNumber(final String text) {
        assertThat(calculator.add(text)).isSameAs(Integer.parseInt(text));
    }

    @DisplayName(value = "숫자 두개를 쉼표(,) 구분자로 입력할 경우 두 숫자의 합을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1,2"})
    void twoNumbers(final String text) {
        assertThat(calculator.add(text)).isSameAs(3);
    }

    @DisplayName(value = "구분자를 쉼표(,) 이외에 콜론(:)을 사용할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"1,2:3", "1,2,3", "1:2:3"})
    void colons(final String text) {
        assertThat(calculator.add(text)).isSameAs(6);
    }

    @DisplayName(value = "//와 \\n 문자 사이에 커스텀 단일 문자로 된 구분자를 지정할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;2;3", "//E\n1E2E3", "//+\n1+2+3", "//@\n1@2@3"})
    void customDelimiter(final String text) {
        assertThat(calculator.add(text)).isSameAs(6);
    }


    @DisplayName(value = "//와 \\n 문자 사이에 커스텀 여러 문자로 구성된 구분자를 지정할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"//lol\n1lol2lol3", "//EE\n1EE2EE3", "//+_\n1+_2+_3", "//ER\n1ER2ER3"})
    void customDelimiter2(final String text) {
        assertThat(calculator.add(text)).isSameAs(6);
    }


    @DisplayName(value = "문자열 계산기에 음수를 전달하는 경우 RuntimeException 예외 처리를 한다.")
    @ParameterizedTest
    @ValueSource(strings = {"-1", "//;\n-1;1;1", "1,-1,1", "1:1:-1", "1:-1,1", "1:1,-1", "-1:-1,-1", "-1:-1,1"})
    void negative(final String text) {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> calculator.add(text));
    }

    @DisplayName(value = "문자열 계산기에 숫자가 아닌 문자를 전달하는 경우 RuntimeException 예외 처리를 한다.")
    @ParameterizedTest
    @ValueSource(strings = {"//;\na;b;c", "a,b,c", "a:b:c", "a:b,c", "1:b,c", "a:b,1", "1:b,1"})
    void notInteger(final String text) {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> calculator.add(text));
    }
}
