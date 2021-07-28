package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

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

    @DisplayName(value = "숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환한다.")
    @ParameterizedTest(name = "{0} 입력")
    @ValueSource(strings = {"1","10","100"})
    void oneNumber(final String text) {
        StringCalculator calculator = new StringCalculator();
        assertThat(calculator.add(text)).isSameAs(Integer.parseInt(text));
    }
}
