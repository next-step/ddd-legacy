package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;

class InputValidatorTest {

    @ParameterizedTest
    @DisplayName("빈 문자열 또는 null을 입력할 경우 true를 반환한다.")
    @NullAndEmptySource
    void inputEmptyStringOrNullReturnZero(String text) {
        assertThat(InputValidator.validation(text)).isTrue();
    }
}
