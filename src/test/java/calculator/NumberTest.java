package calculator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static calculator.Number.NEGATIVE_NUMBER_EXCEPTION;
import static calculator.Number.PARSING_INTEGER_EXCEPTION;
import static calculator.Number.ZERO_NUMBER;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("음이 아닌 숫자 클래스를 위한 테스트")
class NumberTest {
    @DisplayName("문자열이 숫자가 아니면 예외가 발생한다.")
    @ValueSource(strings = {"a", "string", "-", "+"})
    @ParameterizedTest
    void invalidParsing(String value) {
        Assertions.assertThatThrownBy(() -> Number.from(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(PARSING_INTEGER_EXCEPTION);
    }

    @DisplayName("입력 받은 값이 음수이면 예외가 발생한다.")
    @ValueSource(strings = {"-1", "-99", "-100"})
    @ParameterizedTest
    void invalidNegativeNumber(String value) {
        Assertions.assertThatThrownBy(() -> Number.from(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(NEGATIVE_NUMBER_EXCEPTION);
    }

    @DisplayName("입력 받은 값이 빈 문자열이나 null이면 0이 반환된다.")
    @NullAndEmptySource
    @ValueSource(strings = {" ", "         "})
    @ParameterizedTest
    void returnZero(String value) {
        assertThat(Number.from(value)).isEqualTo(ZERO_NUMBER);
    }
}
