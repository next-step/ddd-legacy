package calculator;

import calculator.exception.InvalidNumberFormatException;
import calculator.exception.NegativeNumberException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PositiveNumberTest {

    @DisplayName("0 이상의 숫자를 입력하면 객체를 생성한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1", "5", "100", "20000", "0"})
    void valid_number_creates_positive_number(String text) {
        // when
        PositiveNumber positiveNumber = new PositiveNumber(text);

        // then
        assertEquals(Integer.parseInt(text), positiveNumber.getValue());
     }

    @DisplayName("숫자가 아닌 값을 입력하면 InvalidNumberFormatException 예외를 던진다.")
    @ParameterizedTest
    @ValueSource(strings = {"abc", "ddd", "calculator", "devfancy"})
    void non_numeric_value_throws_exception(String text) {
        // when & then
        assertThatThrownBy(() -> new PositiveNumber(text))
            .isInstanceOf(InvalidNumberFormatException.class)
            .hasMessage("Invalid input: Non-numeric value found: " + text);
    }

    @DisplayName("음수를 입력하면 NegativeNumberException 예외를 던진다.")
    @ParameterizedTest
    @ValueSource(strings = {"-1", "-5", "-100", "-20000", "-1000000"})
    void negative_number_throws_exception(String number) {
        // when & then
        assertThatThrownBy(() -> new PositiveNumber(number))
            .isInstanceOf(NegativeNumberException.class)
            .hasMessage("Negative numbers are not allowed: " + number);
    }
}
