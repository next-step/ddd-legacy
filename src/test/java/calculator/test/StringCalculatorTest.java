package calculator.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import calculator.main.Parser;
import calculator.main.StringCalculator;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

public class StringCalculatorTest {

    private StringCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new StringCalculator((new Parser()));
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

    @DisplayName(value = "문자열 계산기에 음수를 전달하는 경우 RuntimeException 예외 처리를 한다.")
    @Test
    void negative() {
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> calculator.add("-1"));
    }

    @DisplayName(value = "숫자 두개를 쉼표(,) 구분자로 입력할 경우 두 숫자의 합을 반환한다.")
    @ParameterizedTest
    @MethodSource("makeTextCommaDelimiter")
    void twoNumbers(final String text, final int sum) {
        assertThat(calculator.add(text)).isSameAs(sum);
    }

    static Stream<Arguments> makeTextCommaDelimiter() {
        return Stream.of(
            Arguments.of("1,2,3,4,5", 15),
            Arguments.of("1,2,3,4,5,6", 21),
            Arguments.of("1,2,3,4,5,6,7", 28)
        );
    }

    @DisplayName(value = "구분자를 쉼표(,) 이외에 콜론(:)을 사용할 수 있다.")
    @ParameterizedTest
    @MethodSource("makeTextAddColonDelimiter")
    void colons(final String text) {
        assertThat(calculator.add(text)).isSameAs(15);
    }

    static Stream<Arguments> makeTextAddColonDelimiter() {
        return Stream.of(
            Arguments.of("1:2,3,4,5"),
            Arguments.of("1,2:3:4,5"),
            Arguments.of("1,2,3:4:5"),
            Arguments.of("1:2:3:4:5")
        );
    }

    @DisplayName(value = "구분자를 쉼표(,)와 콜론(:) 이외에 값을 사용하면 RuntimeException 예외 처리를 한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1,2:3,4,5,6:7@8,9,10"})
    void invalid_delimiter(final String text) {
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> calculator.add(text));
    }

    @DisplayName(value = "구분자만 포함되고 값이 없는 경우, RuntimeException 예외 처리를 한다.")
    @ParameterizedTest
    @ValueSource(strings = {",,:,,,,:::::,:,:,:,,,,"})
    void colons_empty_input(final String text) {
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> calculator.add(text));
    }

    @DisplayName(value = "입력값에 문자가 포함되어 있는 경우, RuntimeException 예외 처리를 한다.")
    @ParameterizedTest
    @ValueSource(strings = {"a,2,4,6,8,:10,12:14,16:18,20"})
    void colons_invalid_input(final String text) {
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> calculator.add(text));
    }

    @DisplayName(value = "입력값에 음수가 포함되어 있는 경우, RuntimeException 예외 처리를 한다.")
    @ParameterizedTest
    @ValueSource(strings = {"99,8:-1,0:5"})
    void colons_negative_input(final String text) {
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> calculator.add(text));
    }

    @DisplayName(value = "//와 \n 문자 사이에 커스텀 구분자를 지정할 수 있다.")
    @ParameterizedTest
    @MethodSource("makeTextIncludeCustomDelimiter")
    void customDelimiter(final String text) {
        assertThat(calculator.add(text)).isSameAs(55);
    }

    static Stream<Arguments> makeTextIncludeCustomDelimiter() {
        return Stream.of(
            Arguments.of("//-\n1-2-3-4-5-6-7-8-9-10"),
            Arguments.of("//+\n1+2+3+4+5+6+7+8+9+10"),
            Arguments.of("//*\n1*2*3*4*5*6*7*8*9*10")
        );
    }

    @DisplayName(value = "커스텀 구분자는 한개를 지정할 수 있으며, 복수개를 지정하는 경우 RuntimeException 예외 처리를 한다.")
    @ParameterizedTest
    @ValueSource(strings = {"//-\n1-2//?\n3-4-5-6-7-8-9-10"})
    void multiple_custom_delimiter(final String text) {
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> calculator.add(text));
    }

    @DisplayName(value = "커스텀 구분자를 사용하면서, 음수를 전달하는 경우 RuntimeException 예외 처리를 한다.")
    @Test
    void negative_custom_delimiter() {
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> calculator.add("//;\n-1;2;3;4;5;6;7;8;9;10"));
    }

    @DisplayName(value = "커스텀 구분자를 사용하면서, 문자를 전달하는 경우 RuntimeException 예외 처리를 한다.")
    @Test
    void invalid_input_custom_delimiter() {
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> calculator.add("//;\n1;invalidinput;3"));
    }

    @DisplayName(value = "커스텀 구분자를 사용하면서, 입력값이 없는 경우 RuntimeException 예외 처리를 한다.")
    @Test
    void empty_input_custom_delimiter() {
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> calculator.add("//;\n;;"));
    }
}
