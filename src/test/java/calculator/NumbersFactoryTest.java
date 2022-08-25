package calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class NumbersFactoryTest {

    private NumbersFactory numbersFactory;
    
    @BeforeEach
    void setUp() {
        this.numbersFactory = new NumbersFactory();
    }

    @DisplayName(value = "숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1"})
    void oneNumber(final String text) {
        assertThat(numbersFactory.getNumbers(text)).containsExactly(1);
    }

    @DisplayName(value = "숫자 두개를 쉼표(,) 구분자로 입력할 경우 두 숫자의 배열을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1,2"})
    void twoNumbers(final String text) {
        assertThat(numbersFactory.getNumbers(text)).containsExactly(1,2);
    }

    @DisplayName(value = "구분자를 쉼표(,) 이외에 콜론(:)을 사용할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"1,2:3"})
    void colons(final String text) {
        assertThat(numbersFactory.getNumbers(text)).containsExactly(1,2,3);
    }

    @DisplayName(value = "//와 \\n 문자 사이에 커스텀 구분자를 지정할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;2;3"})
    void customDelimiter(final String text) {
        assertThat(numbersFactory.getNumbers(text)).containsExactly(1,2,3);
    }

    @DisplayName(value = "문자열 계산기에 음수를 전달하는 경우 RuntimeException 예외 처리를 한다.")
    @Test
    void negative() {
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> numbersFactory.getNumbers("-1"))
            .withMessage(ExceptionMessages.WRONG_INPUT_EXCEPTION);
    }

    @DisplayName(value = "문자열 계산기에 숫자가 아닌 문자를 전달하는 경우 RuntimeException 예외 처리를 한다.")
    @Test
    void character() {
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> numbersFactory.getNumbers("1,a"))
            .withMessage(ExceptionMessages.WRONG_INPUT_EXCEPTION);
    }

}
