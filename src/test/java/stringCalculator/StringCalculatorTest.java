package stringCalculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class StringCalculatorTest {

    private StringCalculator stringCalculator;

    @BeforeEach
    void setUp() {
        stringCalculator = new StringCalculator();
    }

    @DisplayName("빈 문자열 또는 null을 입력할 경우, 0을 반환해야 한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void emptyOrNull(final String text) {
        assertThat(stringCalculator.add(text)).isZero();
    }

    @DisplayName("숫자 하나를 문자열로 입력할 경우, 해당 숫자를 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1"})
    void onlyOneNumber(final String text) {
        assertThat(stringCalculator.add(text)).isSameAs(Integer.parseInt(text));
    }

    @DisplayName("숫자 두 개를 쉼표(,) 구분자로 입력할 경우, 두 숫자의 합을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1,2"})
    void restTwoNumbers(final String text) {
//        assertThat(stringCalculator.add(text)).isSameAs(3);
        assertThat(stringCalculator.add(text)).isEqualTo(3); // isEqualTo는 isSameAs와 달리 assertNotNull 체크하네요.
    }

    @DisplayName("구분자를 쉼표(,) 이외에 콜론(:)을 사용할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"1,2:3"})
    void colonsNumbers(final String text) {
        assertThat(stringCalculator.add(text)).isSameAs(6);
    }

    @DisplayName("//와 \\n 문자 사이에 커스텀 구분자를 지정할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;2;3"})
    void customDelimiter(final String text) {
        assertThat(stringCalculator.add(text)).isSameAs(6);
    }

    @DisplayName("문자열 계산기에 음수를 전달하는 경우, RuntimeException 예외를 처리를 한다.")
    @Test
    void negative() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> stringCalculator.add("-1"));
    }

}