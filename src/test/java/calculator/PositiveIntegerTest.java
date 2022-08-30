package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

class PositiveIntegerTest {
    @DisplayName("음수를 입력할 시 에러 반환")
    @Test
    void negativeValue() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> new PositiveInteger(-1));
    }

    @DisplayName("숫자가 아닌 값을 입력하면 예외를 발생시킨다.")
    @ParameterizedTest
    @ValueSource(strings = {
            "a",
            "1,(",
            "//;\n3@5"
    })
    void illegalInteger(String illegalString) {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> PositiveInteger.parse(illegalString))
                .withMessage("숫자를 입력해주세요.");
    }

    @DisplayName("양수 혹은 0을 입력하면, 입력한 값을 가진 '양수' 인스턴스가 반한된다.")
    @ParameterizedTest
    @CsvSource({
            "'0',0",
            "'1',1",
            "'181',181"
    })
    void positiveOrZero(String validString, int value) {
        final var integer = PositiveInteger.parse(validString);

        assertThat(integer.getValue()).isEqualTo(value);
    }
}
