package addstring;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;

class CalculatorTest {

    @Test
    void calculate_string_general_success() {
        StringCalculator stringCalculator = new StringCalculator();
        int result = stringCalculator.add("1,2");
        assertThat(result).isEqualTo(3);
    }

    @DisplayName("빈 문자열이나 null일 경우 0을 반환한다.")
    @NullAndEmptySource
    @ParameterizedTest
    void calculate_string_empty_and_null(String s) {
        StringCalculator stringCalculator = new StringCalculator();
        int result = stringCalculator.add(s);
        assertThat(result).isEqualTo(0);
    }

    @Test
    void calculate_string_general_with_custom_separator() {
        StringCalculator stringCalculator = new StringCalculator();
        int result = stringCalculator.add("//;\n1;2;3");
        assertThat(result).isEqualTo(6);
    }

}