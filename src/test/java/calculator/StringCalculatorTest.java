package calculator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class StringCalculatorTest {
    private StringCalculator sut;

    @BeforeEach
    void setUp() {
        sut = new StringCalculator();
    }

    @DisplayName("빈 값을 입력할 경우 0을 반환한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void empty(String text) {
        Assertions.assertThat(sut.sum(text)).isEqualTo(0);
    }

    @DisplayName("숫자 하나를 입력할 경우 해당 숫자를 반환한다.")
    @ParameterizedTest
    @CsvSource(value = {"2*2"}, delimiter = '*')
    void single(String text, int expected) {
        Assertions.assertThat(sut.sum(text)).isEqualTo(expected);
    }

    @DisplayName("쉼표(,) 또는 콜론(:)을 구분자로 가지는 문자열을 전달하는 경우 구분자를 기준으로 분리한 각 숫자의 합을 반환한다.")
    @ParameterizedTest
    @CsvSource(value = {"1,2*3", "1,2,3*6", "1,2:3*6"}, delimiter = '*')
    void multiple(String text, int expected) {
        Assertions.assertThat(sut.sum(text)).isEqualTo(expected);
    }

    @DisplayName("앞의 기본 구분자(쉼표, 콜론) 외에 커스텀 구분자를 지정할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;2;3"})
    void customDelimiter(String text) {
        Assertions.assertThat(sut.sum(text)).isEqualTo(6);
    }

    @DisplayName("문자열 계산기에 숫자 이외의 값 또는 음수를 전달하는 경우 RuntimeException 예외를 throw 한다.")
    @ParameterizedTest
    @ValueSource(strings = {"a,b,c", "1,-1"})
    void notNumberOrNegative(String text) {
        Assertions.assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> sut.sum(text));
    }
}
