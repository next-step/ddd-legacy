package calculator.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("PositiveInt 단위 테스트")
class PositiveIntTest {

    @DisplayName("음수를 입력할 경우 예외를 발생한다.")
    @Test
    void negative() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class, () -> new PositiveInt(-1)),
                () -> assertThrows(IllegalArgumentException.class, () -> new PositiveInt("-1"))
        );
    }

    @DisplayName("숫자가 아닌 문자를 입력할 경우 예외를 발생한다.")
    @ParameterizedTest(name = "입력 값: {0}")
    @ValueSource(strings = {
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "ㄱ", "ㄴ", "ㄷ", "ㄹ", "ㅁ", "ㅂ", "ㅅ", "ㅇ", "ㅈ", "ㅊ"
    })
    void notNumber(final String text) {
        assertAll(
                () -> assertThrows(NumberFormatException.class, () -> new PositiveInt(text)),
                () -> assertThrows(NumberFormatException.class, () -> new PositiveInt(text))
        );
    }

    @DisplayName("숫자와 문자를 입력할 경우 예외를 발생한다.")
    @ParameterizedTest(name = "입력 값: {0}")
    @ValueSource(strings = {"1a", "a1", "a1a"})
    void numberAndString(final String text) {
        assertThrows(NumberFormatException.class, () -> new PositiveInt(text));
    }
}
