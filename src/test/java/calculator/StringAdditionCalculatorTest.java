package calculator;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class StringAdditionCalculatorTest {
    private static StringAdditionCalculator calculator;

    @BeforeAll
    static void setUp() {
        calculator = new StringAdditionCalculator();
    }

    @DisplayName("빈 문자열 또는 null을 입력할 경우 0을 반환해야 한다.")
    @Test
    void return_0_if_text_is_empty_string() {
        assertThat(calculator.calculate("   ")).isZero();
        assertThat(calculator.calculate("")).isZero();
        assertThat(calculator.calculate(null)).isZero();
    }

    @DisplayName("숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환한다.")
    @Test
    void return_int_if_text_is_number_as_single_char() {
        assertThat(calculator.calculate("0")).isZero();
        assertThat(calculator.calculate("1")).isEqualTo(1);
        assertThat(calculator.calculate("9")).isEqualTo(9);
    }

    @DisplayName("문자열 계산기에 숫자 이외의 값을 전달하는 경우 RuntimeException 예외를 throw 한다.")
    @Test
    void throw_RuntimeException_if_text_other_than_number() {
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> calculator.calculate("l"));
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> calculator.calculate("일"));
    }

    @DisplayName("숫자 두개를 컴마(,) 구분자로 입력할 경우 두 숫자의 합을 반환한다.")
    @Test
    void return_summed_int_if_numbers_separated_by_comma() {
        assertThat(calculator.calculate("1,0")).isEqualTo(1);
        assertThat(calculator.calculate("1,12")).isEqualTo(13);
    }

    @DisplayName("문자열 계산기에 음수를 전달하는 경우 RuntimeException 예외 처리를 한다.")
    @Test
    void throw_RuntimeException_if_negative_number_included() {
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> calculator.calculate("-1"));
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> calculator.calculate("1,-1"));
    }
}
