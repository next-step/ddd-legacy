package calculator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("입력값 유효성 검증 테스트")
class InputValidatorTest {
    private final InputValidator validator = new InputValidator();

    @DisplayName("빈 문자열 또는 null 값은 유효하지 않다")
    @ParameterizedTest
    @NullAndEmptySource
    void invalidInput(final String input) {
        Assertions.assertThat(validator.isValid(input)).isFalse();
    }

    @DisplayName("정상적인 입력값은 유효하다")
    @ParameterizedTest
    @ValueSource(strings = {"1", "1,2", "1:2:3", "//;\n1;2;3"})
    void validInput(final String input) {
        Assertions.assertThat(validator.isValid(input)).isTrue();
    }
}
