package stringcalculator.operator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultDelimiterOperatorTest {

    @DisplayName("입력값의 합계를 구하는 테스트")
    @CsvSource(value = {"1,2,3@6", "4:5:6@15", "24,55:11@90"}, delimiter = '@')
    @ParameterizedTest
    void sumTest(String given, String expectation) {
        assertThat(new DefaultDelimiterOperator(given).add()).isEqualTo(Integer.parseInt(expectation));
    }

}