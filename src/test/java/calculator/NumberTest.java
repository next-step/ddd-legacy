package calculator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Number 값 객체 테스트")
class NumberTest {

    @DisplayName("문자열을 숫자로 변환할 수 있다")
    @ParameterizedTest
    @ValueSource(strings = {"1", "2", "3", "10", "100"})
    void createFromValidString(String input) {
        Number number = Number.from(input);
        Assertions.assertThat(number.getValue()).isEqualTo(Integer.parseInt(input));
    }

    @DisplayName("숫자가 아닌 문자열로 생성 시 예외가 발생한다")
    @ParameterizedTest
    @ValueSource(strings = {"a", "abc", "1a", "a1", " "})
    void createFromInvalidString(String input) {
        Assertions.assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> Number.from(input))
                .withMessage("숫자가 아닌 값이 포함되어 있습니다.");
    }

    @DisplayName("음수로 생성 시 예외가 발생한다")
    @Test
    void createFromNegativeNumber() {
        Assertions.assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> Number.from("-1"))
                .withMessage("음수는 허용되지 않습니다.");
    }
}