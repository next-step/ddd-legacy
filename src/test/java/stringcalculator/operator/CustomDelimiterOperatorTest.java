package stringcalculator.operator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CustomDelimiterOperatorTest {

    @DisplayName("입력값의 합계를 구하는 테스트")
    @CsvSource(value = {"//;\\n1;2;3$6", "//!\\n4!7!9$20", "//@\\n11@23@74$108"}, delimiter = '$')
    @ParameterizedTest
    void sumTest(String given, String expectation) {
        assertThat(new CustomDelimiterOperator(given).add()).isEqualTo(Integer.parseInt(expectation));
    }

}