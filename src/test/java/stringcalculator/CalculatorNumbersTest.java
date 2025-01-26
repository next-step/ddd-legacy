package stringcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

public class CalculatorNumbersTest {

    @ParameterizedTest
    @DisplayName(value = "컴마나 콜론을 붙이면 숫자의 합을 반환한다.")
    @CsvSource(value = {"1,2,3|6","1:3:5|9"},delimiter = '|')
    void addValueTest(String calculateForm, int result) {
        assertThat(Calculator.calculate(calculateForm)).isEqualTo(result);
    }

    @Test
    @DisplayName(value = "커스텀 구분자를 사용해서 숫자의 합을 반환한다.")
    void customAddValueTest() {
        assertThat(Calculator.calculate("//&\n3&4&5")).isEqualTo(12);
    }

}
