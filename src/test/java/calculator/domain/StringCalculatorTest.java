package calculator.domain;

import calculator.shared.NumberConvertor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class StringCalculatorTest {
    private StringCalculator sut;
    private StringCalculatorInputValidator stringCalculatorInputValidator;
    private StringCalculatorInputParser parser;
    private StringCalculatorDelimiters delimiters;
    private NumberConvertor numberConvertor;

    @BeforeEach
    void setUp() {
        delimiters = StringCalculatorDelimiters.create();
        stringCalculatorInputValidator = new StringCalculatorInputValidator(delimiters);
        parser = new StringCalculatorInputParser(delimiters);
        numberConvertor = new NumberConvertor();
        sut = new StringCalculator(stringCalculatorInputValidator, parser, numberConvertor);
    }

    @DisplayName(value = "빈 문자열, null 또는 공백 값을 입력할 경우 0을 반환해야 한다.")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "      "}) // 공백 문자열 추가
    void emptyOrNull(final String text) {
        assertThat(sut.add(text)).isZero();
    }

    @DisplayName(value = "숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1"})
    void oneNumber(final String text) {
        assertThat(sut.add(text)).isSameAs(Integer.parseInt(text));
    }

    @DisplayName(value = "숫자 두개를 쉼표(,) 구분자로 입력할 경우 두 숫자의 합을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1,2"})
    void twoNumbers(final String text) {
        assertThat(sut.add(text)).isSameAs(3);
    }

    @DisplayName(value = "구분자를 쉼표(,) 이외에 콜론(:)을 사용할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"1,2:3"})
    void colons(final String text) {
        assertThat(sut.add(text)).isSameAs(6);
    }

    @DisplayName(value = "문자열 계산기에 음수를 전달하는 경우 RuntimeException 예외 처리를 한다.")
    @Test
    void negative() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> sut.add("-1"));
    }

    @DisplayName(value = "커스텀 구분자를 추가하여 덧셈을 수행한다.")
    @ParameterizedTest
    @MethodSource("provideCustomDelimiterInputs")
    void customDelimiter(final String text, final int expected) {
        assertThat(sut.add(text)).isSameAs(expected);
    }

    static Object[][] provideCustomDelimiterInputs() {
        return new Object[][] {
                {"//;\n1;2;3", 6},
                {"//v\n4v5v6", 15},
                {"//#\n7#8#9", 24}
        };
    }
}