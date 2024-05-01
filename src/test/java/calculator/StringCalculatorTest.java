package calculator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

public class StringCalculatorTest {

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("빈 문자열 또는 null이 들어오면 0을 반환한다")
    void null_or_empty(String input) {
        int result = StringCalculator.calculate(input);
        assertThat(result).isEqualTo(0);
    }
}
