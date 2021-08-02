package calculator;

import static org.assertj.core.api.Assertions.assertThat;
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
            () -> PositiveNumber.of(text)
        ).isInstanceOf(RuntimeException.class);
    }

    @DisplayName("숫자 이외의 값을 전달 하는 경우 RuntimeException 예외 처리를 한다")
    @ParameterizedTest
    @ValueSource(strings = {"abc"})
    void notNumber(final String text) {
        assertThatThrownBy(
            () -> PositiveNumber.of(text)
        ).isInstanceOf(RuntimeException.class);
    }

    @DisplayName("0이상 int 최대값 이하의 값을 입력하는 경우 OK")
    @ParameterizedTest
    @ValueSource(strings = {"0", "1", "2", "100", "1000", "2100000000"})
    void positiveNumber(final String text) {
        assertThat(PositiveNumber.of(text)
            .getValue())
            .isEqualTo(Integer.parseInt(text));
    }

    @DisplayName("같은 값이면 equals도 같다")
    @ParameterizedTest
    @ValueSource(strings = {"0", "1", "2", "100", "1000", "2100000000"})
    void equals(final String text) {
        final PositiveNumber positiveNumber1 = PositiveNumber.of(text);
        final PositiveNumber positiveNumber2 = PositiveNumber.of(text);
        assertThat(positiveNumber1)
            .isEqualTo(positiveNumber2);
    }

}
