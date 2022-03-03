package stringcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;

class StringCalculatorTest {

    @DisplayName(value = "빈 문자열 또는 null 값을 입력할 경우 0을 반환해야 한다.")
    @NullAndEmptySource
    @ParameterizedTest
    void nullOrEmptyValue(String source) {
        assertThat(new StringCalculator(source).add()).isZero();
    }

    @DisplayName(value = "숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환한다.")
    @Test
    void singleNumber() {

    }

    @DisplayName(value = "숫자 두개를 쉼표 구분자로 입력할 경우 두 숫자의 합을 반환한다.")
    @Test
    void commaSeparatedNumber() {

    }

    @DisplayName(value = "구분자를 쉼표 이외에 콜론:을 사용할 수 있다.")
    @Test
    void colonDelimiter() {

    }

    @DisplayName(value = "//와 \\n 문자 사이에 커스텀 구분자를 지정할 수 있다.")
    @Test
    void customDelimiter() {

    }

    @DisplayName(value = "문자열 계산기에 음수를 전달하는 경우 RuntimeException 예외 처리를 한다.")
    @Test
    void negativeNumber() {
    }

}
