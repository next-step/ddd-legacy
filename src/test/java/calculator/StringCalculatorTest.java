package calculator;

import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

public class StringCalculatorTest {

    @DisplayName("양의 정수가 아닌 값을 입력하면 RuntimeException이 발생해야 한다.")
    @ParameterizedTest
    @ValueSource(strings = {"-1", "a", "Z", ".", "-", ".9", "0.11", "1:-1:1"})
    void validNumber(String value) {
        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> StringCalculator.of(value);

        // then
        assertThatRuntimeException()
                .isThrownBy(throwingCallable);
    }

    @DisplayName("숫자 사이에 쉼표(,) 또는 콜론(:) 으로 구분된 문자열 형식이 아니면 RuntimeException이 발생해야 한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1;2", "3,,3", ":4", "2:2:", ",1", "2,2,"})
    void validSpliter(String value) {
        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> StringCalculator.of(value);

        // then
        assertThatRuntimeException()
                .isThrownBy(throwingCallable);
    }

    @DisplayName("숫자 사이에 쉼표(,) 또는 콜론(:) 으로 구분된 문자열을 입력하면 숫자의 합을 반환한다.")
    @ParameterizedTest
    @CsvSource(value = {"1,2:3|6", "3:4:5|12", "4:2,3|9"}, delimiter = '|')
    void splitNumber(String value, int expected) {
        // when
        StringCalculator stringCalculator = StringCalculator.of(value);

        // then
        assertThat(stringCalculator.sum()).isEqualTo(expected);
    }

    @DisplayName("커스텀 구분자의 형식이 아닌 경우 RuntimeException이 발생해야 한다. 커스텀 구분자는 문자열 앞부분의 “//”와 “\\n” 사이에 위치하는 문자를 커스텀 구분자로 사용한다.")
    @ParameterizedTest
    @ValueSource(strings = {"\n1;2", "//1", "//1\n", "//a3"})
    void validCustomSpliter(String value) {
        // when
        ThrowableAssert.ThrowingCallable throwingCallable = () -> StringCalculator.of(value);

        // then
        assertThatRuntimeException()
                .isThrownBy(throwingCallable);
    }

    @DisplayName("커스텀 구분자를 사용하여 숫자 사이에 구분자로 구분된 문자열을 입력하면 숫자의 합을 반환한다.")
    @ParameterizedTest
    @MethodSource("customDelimiterStrings")
    void testCustomSpliter(String value, int expected) {
        // when
        StringCalculator stringCalculator = StringCalculator.of(value);

        // then
        assertThat(stringCalculator.sum()).isEqualTo(expected);
    }

    private static Stream<Arguments> customDelimiterStrings() {
        return Stream.of(
                Arguments.of("//i\n1i2i3", 6),
                Arguments.of("//-\n3-4-5", 12),
                Arguments.of("//^\n12^1", 13)
        );
    }

    @DisplayName(value = "빈 문자열 또는 null 값을 입력할 경우 0을 반환해야 한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void emptyOrNull(final String text) {
        assertThat(StringCalculator.of(text).sum()).isZero();
    }

    @DisplayName(value = "숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1"})
    void oneNumber(final String text) {
        assertThat(StringCalculator.of(text).sum()).isSameAs(Integer.parseInt(text));
    }

    @DisplayName(value = "숫자 두개를 쉼표(,) 구분자로 입력할 경우 두 숫자의 합을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1,2"})
    void twoNumbers(final String text) {
        assertThat(StringCalculator.of(text).sum()).isSameAs(3);
    }

    @DisplayName(value = "구분자를 쉼표(,) 이외에 콜론(:)을 사용할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"1,2:3"})
    void colons(final String text) {
        assertThat(StringCalculator.of(text).sum()).isSameAs(6);
    }

    @DisplayName(value = "//와 \\n 문자 사이에 커스텀 구분자를 지정할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;2;3"})
    void customDelimiter(final String text) {
        assertThat(StringCalculator.of(text).sum()).isSameAs(6);
    }

    @DisplayName(value = "문자열 계산기에 음수를 전달하는 경우 RuntimeException 예외 처리를 한다.")
    @Test
    void negative() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> StringCalculator.of("-1"));
    }
}
