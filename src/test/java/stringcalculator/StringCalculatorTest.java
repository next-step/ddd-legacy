package stringcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

public class StringCalculatorTest {

    @DisplayName(value = "빈 문자열일 경우 0으로 반환")
    @ParameterizedTest
    @NullAndEmptySource
    void emptyOrNull(final String text) {
        StringCalculator stringCalculator = new StringCalculator(text);
        stringCalculator.add();
        assertThat(stringCalculator.showResult()).isZero();
    }

    @DisplayName("숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환")
    @ParameterizedTest
    @ValueSource(strings = {"1"})
    void oneNumber(final String text){
        StringCalculator stringCalculator = new StringCalculator(text);
        stringCalculator.add();
        assertThat(stringCalculator.showResult()).isSameAs(Integer.parseInt(text));
    }

    @DisplayName(" 숫자 두개를 쉼표(,) 구분자로 입력할 경우 두 숫자의 합을 반환 ")
    @ParameterizedTest
    @ValueSource(strings = {"1,2"})
    void twoNumbers(final String text){
        StringCalculator stringCalculator = new StringCalculator(text);
        stringCalculator.add();
        assertThat(stringCalculator.showResult()).isSameAs(3);
    }

    @DisplayName("구분자를 쉼표(,) 이외에 콜론(:)을 사용할 수 있다")
    @ParameterizedTest
    @ValueSource(strings = {"1,2:3"})
    void colons(final String text){
        StringCalculator stringCalculator = new StringCalculator(text);
        stringCalculator.add();
        assertThat(stringCalculator.showResult()).isSameAs(6);
    }

    @DisplayName("//와 \n 문자 사이에 커스텀 구분자를 지정")
    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;2;3"})
    void customDelimiter(final String text){
        StringCalculator stringCalculator = new StringCalculator(text);
        stringCalculator.add();
        assertThat(stringCalculator.showResult()).isSameAs(6);
    }

    @DisplayName(value = "문자열 계산기에 음수를 전달하는 경우 RuntimeException 예외 처리")
    @Test
    void negative(){

        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> new StringCalculator("-1"));
    }


}
