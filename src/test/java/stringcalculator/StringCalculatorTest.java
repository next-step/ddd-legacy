package stringcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class StringCalculatorTest {

    @DisplayName(value = "빈 문자열 또는 null 값을 입력할 경우 0을 반환해야 한다.")
    @NullAndEmptySource
    @ParameterizedTest
    void testEmptyValue(String nullAndEmpty) {
        StringCalculator stringCalculator = new StringCalculator();
        assertThat(stringCalculator.add(nullAndEmpty)).isZero();
    }

    @DisplayName(value = "숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환한다.")
    @ValueSource(strings = {"1","2","3"})
    @ParameterizedTest
    void testSingleNumber(String singleNumber) {
        StringCalculator stringCalculator = new StringCalculator();
        assertThat(stringCalculator.add(singleNumber)).isEqualTo(Integer.parseInt(singleNumber));

    }

    @DisplayName(value = "숫자 두개를 쉼표(,) 구분자로 입력할 경우 두 숫자의 합을 반환한다.")
    @ValueSource(strings = {"1,2"})
    @ParameterizedTest
    void testCommaSeparatedNumber(String source) {
        StringCalculator stringCalculator = new StringCalculator();
        assertThat(stringCalculator.add(source)).isEqualTo(3);
    }

    @DisplayName(value = "구분자를 쉼표(,) 이외에 콜론(:)을 사용할 수 있다.")
    @ValueSource(strings = {"3:4"})
    @ParameterizedTest
    void testColonDelimiter(String source) {
        StringCalculator stringCalculator = new StringCalculator();
        assertThat(stringCalculator.add(source)).isEqualTo(7);

    }

    @DisplayName(value = "//와 \\n 문자 사이에 커스텀 구분자를 지정할 수 있다.")
    @Test
    void testCustomDelimiter() {

    }

    @DisplayName(value = "문자열 계산기에 음수를 전달하는 경우 RuntimeException 예외 처리를 한다.")
    @ValueSource(strings = {"-1", "-2", "-10"})
    @ParameterizedTest
    void testNegativeNumber(String negativeNumber) {
        StringCalculator stringCalculator = new StringCalculator();
        assertThatThrownBy(() -> stringCalculator.add(negativeNumber)).isInstanceOf(RuntimeException.class);
    }
}
