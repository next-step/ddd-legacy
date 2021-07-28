package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("문자열 계산기 테스트")
public class StringCalculatorTest {

    @DisplayName(value = "빈 문자열 또는 null 값을 입력할 경우 0을 반환해야 한다.")
    @ParameterizedTest(name = "{0} 입력")
    @NullAndEmptySource
    void emptyOrNull(final String text) {
        StringCalculator calculator = new StringCalculator();
        assertThat(calculator.add(text)).isZero();
    }
}
