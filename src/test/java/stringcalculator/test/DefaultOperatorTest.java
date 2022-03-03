package stringcalculator.test;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import stringcalculator.operator.DefaultOperator;

class DefaultOperatorTest {

    @DisplayName("합을 구하는 테스트")
    @CsvSource(value = {"1,1,1^3", "2:3:4:1^10"}, delimiter = '^')
    @ParameterizedTest
    void sum(String given, String expectation) {
        Assertions.assertThat(DefaultOperator.from(given).add()).isEqualTo(Integer.parseInt(expectation));
    }

}
