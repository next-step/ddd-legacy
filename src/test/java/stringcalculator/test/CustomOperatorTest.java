package stringcalculator.test;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import stringcalculator.operator.CustomOperator;

class CustomOperatorTest {

    @DisplayName("합을 구하는 테스트")
    @CsvSource(value = {"//~\\n1~1~1#3", "//!\\n2!7!9!1#19", "//&\\n3&23&4#30"}, delimiter = '#')
    @ParameterizedTest
    void sum(String given, String expectation) {
        Assertions.assertThat(CustomOperator.from(given).add()).isEqualTo(Integer.parseInt(expectation));
    }

}
