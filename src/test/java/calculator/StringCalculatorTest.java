package calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class StringCalculatorTest {

    private StringCalculator calculator;

    @BeforeEach
    void setUp(){
        calculator = new StringCalculator();
    }

    @DisplayName(value = "빈 문자열 또는 null 값을 입력한 경우 0을 반환해야 한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void emptyOrNull(final String text){
        assertThat(calculator.add(text)).isZero();
    }

    @DisplayName(value = "숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환한다.")
    @Test
    void oneNumber(){
        assertThat(calculator.add("1")).isSameAs(Integer.parseInt("1"));
    }

    @DisplayName(value = "숫자 두개를 쉼표(,) 구분자로 입력할 경우 두 숫자의 합을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1,2"})
    void twoNumber(final String text){
        assertThat(calculator.add(text)).isSameAs(3);
    }

    @DisplayName(value = "구분자를 쉼표(,) 이외에 콜론(:)을 사용 할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"1,2:3"})
    void colons(final String text){
        assertThat(calculator.add(text)).isSameAs(6);
    }

    @DisplayName(value = "//와 \\n 문자 사이에 커스텀 구분자를 지정 할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;2;3"})
    void customDelimiter(final String text){
        assertThat(calculator.add(text)).isSameAs(6);
    }

    @DisplayName(value = "숫자가 아닌 문자를 전달하는 경우에는 RuntimeException 예외 처리를 한다.")
    @Test
    void notNumber(){
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> calculator.add("a"));
    }

    @DisplayName(value = "문자열 계산기에 음수를 전달하는 경우에는 RuntimeException 예외 처리를 한다.")
    @Test
    void negative(){
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> calculator.add("-1"));
    }

}
