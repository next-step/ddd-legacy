package stringcalculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatRuntimeException;

class StringCalculatorTest {

    StringCalculator stringCalculator;

    @BeforeEach
    void init() {
        stringCalculator = new StringCalculator();
    }

    @DisplayName(value = "입력값이 null 이거나 빈 값이면 0을 리턴한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void nullAndEmpty(final String input) {
        // when
        Integer sut = stringCalculator.calculate(input);

        // then
        assertThat(sut).isZero();
    }

    @DisplayName(value = "숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1", "11"})
    void oneNumber(final String input) {
        // when
        Integer sut = stringCalculator.calculate(input);

        // then
        assertThat(sut).isEqualTo(Integer.parseInt(input));
    }

    @DisplayName(value = "숫자 두개를 , 를 이용하여서 입력하면 합산 된 값을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1,2"})
    void twoCommaNumber(final String input) {
        // when
        Integer sut = stringCalculator.calculate(input);

        // then
        assertThat(sut).isEqualTo(3);
    }

    @DisplayName(value = "숫자 두개를 : 를 이용하여서 입력하면 합산 된 값을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1:2"})
    void twoColonNumber(final String input) {
        // when
        Integer sut = stringCalculator.calculate(input);

        // then
        assertThat(sut).isEqualTo(3);
    }

    @DisplayName(value = "숫자 세개를 , 와 : 를 이용하여서 입력하면 합산 된 값을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1:2,3"})
    void threeNumber(final String input) {
        // when
        Integer sut = stringCalculator.calculate(input);

        // then
        assertThat(sut).isEqualTo(6);
    }

    @DisplayName(value = "//와 \n 문자 사이에 커스텀 구분자를 지정할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"//asdf\n1asdf2asdf3"})
    void customSeparatorNumber(final String input) {
        // when
        Integer sut = stringCalculator.calculate(input);

        // then
        assertThat(sut).isEqualTo(6);
    }

    @DisplayName(value = "음수가 포함되어 있으면 예외가 발생한다.")
    @ParameterizedTest
    @ValueSource(strings = {"-1"})
    void negativeNumber(final String input) {
        // when
        // then
        assertThatRuntimeException()
            .isThrownBy(() -> stringCalculator.calculate(input));
    }
}