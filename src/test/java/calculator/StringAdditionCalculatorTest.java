package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;

class StringAdditionCalculatorTest {

    @DisplayName("빈 문자열 또는 null 을 입력할 경우 0을 반환")
    @NullAndEmptySource
    @ParameterizedTest(name = "{0} 이 들어오면 0의 값을 반환")
    void add01(String input) {
        int result = StringAdditionCalculator.calculate(input);

        assertThat(result).isEqualTo(0);
    }

    @DisplayName("쉼표(,) 또는 콜론(:)을 구분자로 가지는 문자열 전달할 경우 숫자의 합 반환")
    @ParameterizedTest(name = "{0} = {1}")
    @CsvSource(value = {"1,2|3", "1,2,3|6", "1,2:3|6"}, delimiterString = "|")
    void add02(String input, int result) {
        int calculatedResult = StringAdditionCalculator.calculate(input);

        assertThat(calculatedResult).isEqualTo(result);
    }
}
