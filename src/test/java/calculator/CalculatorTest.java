package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CalculatorTest {
    @DisplayName("쉼표 또는 콜론을 구분자로 이용하여 덧셈을 한다.")
    @Test
    void add() {
        Calculator calculator = new Calculator();

        int sum = calculator.add("1,2:3");

        assertThat(sum).isEqualTo(6);
    }

    @DisplayName("숫자가 하나만 존재하면 해당 숫자를 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1:", "1,", "1"})
    void add_with_one_number(String numbers) {
        Calculator calculator = new Calculator();

        int sum = calculator.add(numbers);

        assertThat(sum).isEqualTo(1);
    }

    @DisplayName("첫 문자가 숫자가 아니면 안된다.")
    @ParameterizedTest
    @ValueSource(strings = {":1", ",1"})
    void add_none_number_start(String numbers) {
        Calculator calculator = new Calculator();

        assertThatThrownBy(() -> calculator.add(numbers))
                .isInstanceOf(NumberFormatException.class);
    }

    @DisplayName("입력하는 숫자는 비어있을 수 없다.")
    @ParameterizedTest
    @NullAndEmptySource
    void add_with_empty(String numbers) {
        Calculator calculator = new Calculator();

        int sum = calculator.add(numbers);

        assertThat(sum).isEqualTo(0);
    }

    @DisplayName(value = "//와 \\n 문자 사이에 커스텀 구분자를 지정할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;2;3", "//!\n1!2!3"})
    void add_with_custom_delimiter(final String numbers) {
        Calculator calculator = new Calculator();

        int sum = calculator.add(numbers);

        assertThat(sum).isEqualTo(6);
    }

    @DisplayName("음수는 입력할 수 없습니다.")
    @Test
    void add_with_negative() {
        Calculator calculator = new Calculator();

        assertThatThrownBy(() -> calculator.add("-1"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
