package calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class CalculatorTest {
    private Calculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new Calculator();
    }

    @ParameterizedTest(name = "빈 문자열 또는 null 값을 입력할 경우 0을 반환해야 한다. [text={0}]")
    @NullAndEmptySource
    void emptyOrNull(final String text) {
        var result = calculator.add(text);

        assertThat(result).isZero();
    }

    @ParameterizedTest(name = "숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환한다. [text={0}]")
    @ValueSource(strings = {"30"})
    void oneNumber(final String text) {
        var result = calculator.add(text);

        assertThat(result).isSameAs(Integer.parseInt(text));
    }

    @ParameterizedTest(name = "숫자 두개를 쉼표(,) 구분자로 입력할 경우 두 숫자의 합을 반환한다. [text={0}]")
    @ValueSource(strings = {"1,2"})
    void twoNumbers(final String text) {
        var result = calculator.add(text);

        assertThat(result).isSameAs(3);
    }

    @ParameterizedTest(name = "구분자를 쉼표(,) 이외에 콜론(:)을 사용할 수 있다. [text={0}]")
    @ValueSource(strings = {"1,2:3"})
    void colons(final String text) {
        var result = calculator.add(text);

        assertThat(result).isSameAs(6);
    }

    @ParameterizedTest(name = "//와 \\n 문자 사이에 커스텀 구분자를 지정할 수 있다. [text={0}]")
    @ValueSource(strings = {"//;\n1;2;3"})
    void customDelimiter(final String text) {

        var result = calculator.add(text);

        assertThat(result).isSameAs(6);
    }

    @DisplayName(value = "문자열 계산기에 음수를 전달하는 경우 RuntimeException 예외 처리를 한다.")
    @Test
    void negative() {
        var exception = catchThrowable(() -> calculator.add("-1"));

        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}
