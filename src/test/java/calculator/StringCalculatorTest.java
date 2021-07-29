package calculator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.thymeleaf.util.StringUtils;

public class StringCalculatorTest {

    final StringCalculator calculator = new StringCalculator();

    @DisplayName("빈 문자열을 입력하는 경우 0을 반환한다")
    @ParameterizedTest
    @NullAndEmptySource
    void emptyOrNull(final String text) {
        final int expect = calculator.calculate(text);
        assertThat(expect).isZero();
    }

    @DisplayName("blank 문자열을 입력하는 경우 0을 반환한다")
    @ParameterizedTest
    @ValueSource(strings = {" ", "  ", "   "})
    void blank(final String text) {
        final int expect = calculator.calculate(text);
        assertThat(expect).isZero();
    }

    @DisplayName("숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환한다")
    @ParameterizedTest
    @ValueSource(strings = {"1", "2", "3"})
    void oneNumber(final String text) {
        final int expect = calculator.calculate(text);
        assertThat(expect).isEqualTo(Integer.parseInt(text));
    }

    @DisplayName("숫자 두개를 컴마(,) 구분자로 입력할 경우 두 숫자의 합을 반환한다")
    @ParameterizedTest
    @ValueSource(strings = {"1,2"})
    void twoNumberWithComma(final String text) {
        final int expect = calculator.calculate(text);
        assertThat(expect).isEqualTo(3);
    }

    @DisplayName("구분자를 컴마(,) 이외에 콜론(:)을 사용할 수 있다")
    @ParameterizedTest
    @ValueSource(strings = {"1,2:3"})
    void colon(final String text) {
        final int expect = calculator.calculate(text);
        assertThat(expect).isEqualTo(6);
    }

    @DisplayName("//와 \\n문자 사이에 커스텀 구분자를 지정할 수 있다")
    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;2;3"})
    void customDelimiter(final String text) {
        final int expect = calculator.calculate(text);
        assertThat(expect).isEqualTo(6);
    }

    @DisplayName("커스텀 구분자와 기존 구분자를 함께 사용할 수 있다")
    @ParameterizedTest
    @ValueSource(strings = {"//;\n1,2:3;4"})
    void withCustomDelimiter(final String text) {
        final int expect = calculator.calculate(text);
        assertThat(expect).isEqualTo(10);
    }

    @Test
    void whiteSpace() {
        assertThat(StringUtils.isEmpty(null)).isTrue();
        assertThat(StringUtils.isEmpty("")).isTrue();
        assertThat(StringUtils.isEmpty(" ")).isFalse();

        assertThat(org.springframework.util.StringUtils.hasText(null)).isFalse();
        assertThat(org.springframework.util.StringUtils.hasText("")).isFalse();
        assertThat(org.springframework.util.StringUtils.hasText(" ")).isFalse();
    }

}
