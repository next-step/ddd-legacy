package calculator;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * <요구사항>
 * 1. 구분자를 가지는 문자열을 전달 받는다.
 * 2. 기본 구분자([",", ":"])를 기준으로 분리한 각 숫자의 합을 반환한다.
 * 3. 기본 구분자 외에 커스텀 구분자를 지정할 수 있다. 커스텀 구분자는 "//"와 "\n"사이에 위치하는 '문자'를 커스텀 구분자로 사용한다.
 * 4. 문자열 계산기에 숫자 이외의 값 또는 음수를 전달하는 경우. RuntimeException 예외를 throw 한다.
 * <p>
 * <요구사항 - 정제>
 * 1.문자열을 받는다.
 * 2.문자열은 구분자와 숫자로 구분된다.
 * 3.구분자는 "기본 구분자"와 "커스텀 구분자" 2가지로 구성된다.
 * 3-1. "기본 구분자" [",", ":"]
 * 3-2. "커스텀 구분자" "//"(문자)"/n"
 * 4. 숫자는 0을 포함하는 자연수
 * 5. 4이외의 수가 전달되는 경우 RuntimeException 예외를 throw한다.
 */
class StringCalculatorTest {
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
    @ValueSource(strings = {"1"})
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
    @ValueSource(strings = {"1,2:3"})
    void colons(final String text) {
        assertThat(calculator.add(text)).isSameAs(6);
    }

    @DisplayName(value = "//와 \\n 문자 사이에 커스텀 구분자를 지정할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;2;3"})
    void customDelimiter(final String text) {
        assertThat(calculator.add(text)).isSameAs(6);
    }

    @DisplayName(value = "//와 \\n 문자 사이에 커스텀 구분자를 지정할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;2;3"})
    void findCustomDelimiter(final String text) {
        String[] divided = calculator.initSeparator(text);
        assertThat(divided[0]).isEqualTo(";");
    }

    @DisplayName(value = "문자열 계산기에 음수를 전달하는 경우 RuntimeException 예외 처리를 한다.")
    @Test
    void negative() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> calculator.add("-1"));
    }
}
