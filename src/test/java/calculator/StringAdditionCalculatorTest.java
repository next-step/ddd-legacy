package calculator;

import calculator.exception.InvalidNumberFormatException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import calculator.exception.NegativeNumberException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class StringAdditionCalculatorTest {

    @DisplayName("구분자를 포함한 문자열의 합을 계산한다.")
    @Test
    void calculator_sum_of_string_with_delimiter() {
        // given
        StringAdditionCalculator calculator1 = new StringAdditionCalculator("1,2");
        StringAdditionCalculator calculator2 = new StringAdditionCalculator("1,2:3");

        // when & then
        assertEquals(3, calculator1.add());
        assertEquals(6, calculator2.add());

    }

    @DisplayName("만약 빈 문자열 또는 null을 입력할 경우, 0을 반환한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void if_input_string_is_empty_or_null_then_return_0(final String input) {
        // given
        StringAdditionCalculator calculator = new StringAdditionCalculator(input);

        // when
        int result = calculator.add();

        // then
        assertEquals(0, result);
    }

    @DisplayName("커스텀 구분자를 포함한 문자열의 합을 계산한다")
    @Test
    void calculator_sum_of_string_with_custom_delimiter() {
        // given
        StringAdditionCalculator calculator1 = new StringAdditionCalculator("//;\n1;2;3");
        StringAdditionCalculator calculator2 = new StringAdditionCalculator("//\\*\n4*5*6");
        StringAdditionCalculator calculator3 = new StringAdditionCalculator("//\\.\n10.11.12");

        // when & then
        assertEquals(6, calculator1.add());
        assertEquals(15, calculator2.add());
        assertEquals(33, calculator3.add());
    }

    @DisplayName("숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환한다.")
    @Test
    void if_single_number_is_provided_then_return_number() {
        // given
        StringAdditionCalculator calculator1 = new StringAdditionCalculator("1");
        StringAdditionCalculator calculator2 = new StringAdditionCalculator("0");

        // when
        int result1 = calculator1.add();
        int result2 = calculator2.add();

        // then
        assertEquals(1, result1);
        assertEquals(0, result2);
    }

    @DisplayName("숫자가 아닌 값이 포함된 문자열을 전달할 경우 예외를 던진다.")
    @Test
    void if_non_numeric_value_is_provided_then_throw_exception() {
        // given
        StringAdditionCalculator calculator = new StringAdditionCalculator("1,text,3");

        // when & then
        assertThatThrownBy(calculator::add)
            .isInstanceOf(InvalidNumberFormatException.class)
            .hasMessage("Invalid input: Non-numeric value found: text");
    }

    @DisplayName("음수 값이 포함된 문자열을 전달할 경우 예외를 던진다.")
    @Test
    void if_negative_number_is_provided_then_throw_exception() {
        // given
        StringAdditionCalculator calculator = new StringAdditionCalculator("1,-2,3");

        // when & then
        assertThatThrownBy(calculator::add)
            .isInstanceOf(NegativeNumberException.class)
            .hasMessage("Negative numbers are not allowed: -2");
    }

}
