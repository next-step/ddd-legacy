package mission.step1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static mission.step1.StringCalculator.EMPTY_EXPRESSION;
import static mission.step1.StringCalculator.ZERO_VALUE;
import static org.junit.jupiter.api.Assertions.*;


class StringCalculatorTest {

    private StringCalculator stringCalculator;

    @BeforeEach
    void setUp() {
        stringCalculator = new StringCalculator();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1:2:3",
            "1,2,3",
            "1:2,3"
    })

    void splitStringExpression(String input) {
        String[] result = stringCalculator.splitWithDelimiter(input);

        // 모든 케이스에서 결과는 ["1", "2", "3"]이어야 함
        assertArrayEquals(
                new String[]{"1", "2", "3"},
                result
        );
    }

    @Test
    @DisplayName("음수 입력시 RuntimeException 발생")
    void throwExceptionWhenNegativeNumber() {
        assertThrows(RuntimeException.class,
                () -> stringCalculator.toInt("-1"),
                "음수 입력시 예외가 발생해야 합니다");
    }

    @Test
    @DisplayName("숫자가 아닌 입력시 RuntimeException 발생")
    void throwExceptionWhenNotNumber() {
        assertThrows(RuntimeException.class,
                () -> stringCalculator.toInt("abc"),
                "숫자가 아닌 입력시 예외가 발생해야 합니다");
    }

    @Test
    @DisplayName("올바른 양수 입력시 해당 숫자 반환")
    void returnNumberWhenValidInput() {
        assertEquals(123, stringCalculator.toInt("123"));
    }

    @Test
    @DisplayName("빈 문자열 입력시 0 반환")
    void emptyStringTest() {
        assertEquals(ZERO_VALUE, stringCalculator.hasText(EMPTY_EXPRESSION));
    }

    @Test
    @DisplayName("숫자 문자열 입력시 해당 값 반환")
    void numberStringTest() {
        assertEquals(123, stringCalculator.hasText("123"));
    }


}