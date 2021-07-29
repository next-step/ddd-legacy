package calculator;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class PositiveNumberTest {

    @DisplayName("음수를 전달하는 경우 RuntimeException 예외 처리를 한다")
    @ParameterizedTest
    @ValueSource(strings = {"-1"})
    void negativeNumber(final String text) {
        assertThatThrownBy(
            () -> new PositiveNumber(text)
        ).isInstanceOf(RuntimeException.class);
    }

}
