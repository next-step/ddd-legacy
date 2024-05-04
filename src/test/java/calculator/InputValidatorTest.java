package calculator;

import org.hibernate.annotations.Source;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class InputValidatorTest {

    static Stream<Arguments> inputProvider() {
        return Stream.of(
                Arguments.of(null, true),
                Arguments.of("", true),
                Arguments.of("    ", true)
        );
    }

    @ParameterizedTest
    @DisplayName("빈 문자열 또는 null을 입력할 경우 true를 반환한다.")
    @MethodSource("inputProvider")
    void inputEmptyStringOrNullReturnZero(String text, boolean expected) {
        assertThat(InputValidator.validation(text)).isEqualTo(expected);
    }
}
