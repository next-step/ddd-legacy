package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StringCalculatorTest {

    @Test
    @DisplayName("입력된 문자열의 합을 계산한다.")
    void testAdd() {
        // given
        final String text = "1,2";

        // when
        final int result = StringCalculator.add(text);

        // then
        assertEquals(3, result);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("입력이 null 또는 빈 문자열일 경우 0을 반환한다.")
    void testReturn0WhenEmptyText(final String text) {
        // when
        final int result = StringCalculator.add(text);

        // then
        assertEquals(0, result);
    }

    @Test
    @DisplayName("기본 구분자는 `,`, `:`를 사용한다.")
    void testCheckDelimiter() {
        // given
        final String text = "1,2:3";

        // when
        final int result = StringCalculator.add(text);

        // then
        assertEquals(6, result);
    }

    @Test
    @DisplayName("`//`와 `\\n` 사이의 문자를 커스텀 구분자로 사용한다.")
    void testCustomDelimiter() {
        // given
        final String text = "//;\\n1;2;3";

        // when
        final int result = StringCalculator.add(text);

        // then
        assertEquals(6, result);
    }

    @Test
    @DisplayName("음수를 전달하면 RuntimeException이 발생한다.")
    void testNegativeNumber() {
        // given
        final String text = "1;2;-3";

        // when & then
        assertThrows(RuntimeException.class, () -> StringCalculator.add(text));
    }

    @Test
    @DisplayName("숫자 이외의 값을 전달하면 RuntimeException이 발생한다.")
    void testNotNumericValue() {
        // given
        final String text = "1;a;3";

        // when & then
        assertThrows(RuntimeException.class, () -> StringCalculator.add(text));
    }

}
