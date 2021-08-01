package calculator.number;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

@DisplayName("PositiveNumber 테스트")
public class PositiveNumberTest {

    @DisplayName(value = "음수일 경우, RuntimeException 발생한다")
    @ParameterizedTest(name = "음수 {0}인 경우, RuntimeException 발생")
    @ValueSource(strings = {"-1","-2","-10"})
    void negative(final String text) {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> new PositiveNumber(text));
    }

    @DisplayName(value = "숫자가 아닌 문자인 경우, RuntimeException 발생한다")
    @ParameterizedTest(name = "{0} 문자인 경우, RuntimeException 발생")
    @ValueSource(strings = {"테스트","abc","we2"})
    void text(final String text) {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> new PositiveNumber(text));
    }
}
