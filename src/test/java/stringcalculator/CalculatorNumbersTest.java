package stringcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

public class CalculatorNumbersTest {


    private static final String CUSTOM_OPERAND = "//&\n3&4&5";
    public static final int CUSTOM_RESULT = 12;

    @ParameterizedTest
    @DisplayName(value = "컴마나 콜론을 붙이면 숫자의 합을 반환한다.")
    @CsvSource(value = {"1,2,3|6","1:3:5|9"},delimiter = '|')
    void addValueTest(String calculateForm, int result) {
        CalculatorNumbers calculatorNumbers = new CalculatorNumbers(calculateForm);
        assertThat(calculatorNumbers.sum()).isEqualTo(result);
    }

    @Test
    @DisplayName(value = "커스텀 구분자를 사용해서 숫자의 합을 반환한다.")
    void customAddValueTest() {
        CalculatorNumbers calculatorNumbers = new CalculatorNumbers(CUSTOM_OPERAND);
        assertThat(calculatorNumbers.sum()).isEqualTo(CUSTOM_RESULT);
    }

}
