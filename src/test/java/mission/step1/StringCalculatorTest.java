package mission.step1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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

    @ParameterizedTest
    @DisplayName("쉼표 구분자를 사용한 덧셈")
    @CsvSource({
            "'1,2', 3",
            "'1,2,3', 6",
            "'1,2,3,4', 10"
    })
    void addWithCommaDelimiter(String input, int expected) {
        assertEquals(expected, stringCalculator.add(input));
    }

    @ParameterizedTest
    @DisplayName("콜론 구분자를 사용한 덧셈")
    @CsvSource({
            "1:2, 3",
            "1:2:3, 6",
            "1:2:3:4, 10"
    })
    void addWithColonDelimiter(String input, int expected) {
        assertEquals(expected, stringCalculator.add(input));
    }

    @ParameterizedTest
    @DisplayName("혼합 구분자를 사용한 덧셈")
    @CsvSource({
            "'1,2:3', 6",
            "'1:2,3', 6",
            "'1:2,3:4', 10"
    })
    void addWithMixedDelimiters(String input, int expected) {
        assertEquals(expected, stringCalculator.add(input));
    }

    @ParameterizedTest
    @DisplayName("잘못된 커스텀 구분자 형식에 대한 예외 발생")
    @ValueSource(strings = {
            "/;\n1;2;3",     // '//' 누락
            "//;1;2;3",      // '\n' 누락
            "//\n1;2;3",     // 구분자 누락
            ";1;2;3"         // 전체 형식 누락
    })
    void throwExceptionWhenInvalidFormat(String input) {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> stringCalculator.splitWithCustomDelimiter(input));

        assertEquals("커스텀 구분자 형식이 올바르지 않습니다", exception.getMessage());
    }

    @Test
    @DisplayName("정상적인 커스텀 구분자는 예외가 발생하지 않음")
    void noExceptionWithValidDelimiter() {
        String input = "//;\n1;2;3";

        assertAll(
                () -> assertDoesNotThrow(() -> stringCalculator.splitWithCustomDelimiter(input)),
                () -> assertArrayEquals(
                        new String[]{"1", "2", "3"},
                        stringCalculator.splitWithCustomDelimiter(input)
                )
        );
    }

    @Test
    @DisplayName("커스텀 구분자 테스트")
    void customDelimiterTest() {

        assertAll(
                () -> assertDoesNotThrow(() -> stringCalculator.splitWithCustomDelimiter("//;\n1;2;3")),

                () -> assertThrows(RuntimeException.class,
                        () -> stringCalculator.splitWithCustomDelimiter("//;;\n1;2;3")),

                () -> assertThrows(RuntimeException.class,
                        () -> stringCalculator.splitWithCustomDelimiter("//;1;2;3")),

                () -> assertThrows(RuntimeException.class,
                        () -> stringCalculator.splitWithCustomDelimiter("/;1;2;3"))
        );
    }

    @ParameterizedTest
    @DisplayName("구분자가 2글자 이상인 경우 예외 발생")
    @ValueSource(strings = {
            "//;;\n1;;2;;3",    // 2글자
            "//;;;\n1;;;2;;;3",  // 3글자
            "//####\n1####2",    // 4글자
            "//@@@@@\n1@@@@@2"   // 5글자
    })
    void throwExceptionWhenDelimiterTooLong(String input) {
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> stringCalculator.splitWithCustomDelimiter(input));

        assertEquals("커스텀 구분자의 길이는 2를 넘을 수 없습니다", exception.getMessage());
    }
}