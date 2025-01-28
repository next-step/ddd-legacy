package mission.step1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
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
                () -> PositiveNumber.from("-1"),
                "음수 입력시 예외가 발생해야 합니다");
    }

    @Test
    @DisplayName("숫자가 아닌 입력시 RuntimeException 발생")
    void throwExceptionWhenNotNumber() {
        assertThrows(RuntimeException.class,
                () -> PositiveNumber.from("abc"),
                "숫자가 아닌 입력시 예외가 발생해야 합니다");
    }

    @Test
    @DisplayName("올바른 양수 입력시 해당 숫자 반환")
    void returnNumberWhenValidInput() {
        assertEquals(123,
                PositiveNumber.from("123").getValue());
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

    @ParameterizedTest
    @DisplayName("커스텀 구분자를 사용한 덧셈")
    @CsvSource({
            "'//;\n1;2;3', 6",
            "'//@\n1@2@3', 6",
            "'//#\n1#2#3', 6"
    })
    void addWithCustomDelimiter(String input, int expected) {
        assertEquals(expected, stringCalculator.add(input));
    }

    @Test
    @DisplayName("구분자 길이가 2 이상일 때 예외 발생")
    void throwExceptionWhenLengthMoreThanOne() {
        RuntimeException e = assertThrows(RuntimeException.class,
                () -> stringCalculator.validateCustomDelimiterLength(";;", 2));

        assertEquals("커스텀 구분자의 길이는 2를 넘을 수 없습니다", e.getMessage());
    }

    @Test
    @DisplayName("빈 구분자일 때 예외 발생")
    void throwExceptionWhenEmpty() {
        RuntimeException e = assertThrows(RuntimeException.class,
                () -> stringCalculator.validateCustomDelimiterLength("", 2));

        assertEquals("커스텀 구분자의 길이는 1보다 작을 수 없습니다.", e.getMessage());
    }

    @DisplayName(value = "빈 문자열 또는 null 값을 입력할 경우 0을 반환해야 한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void emptyOrNull(final String text) {
        assertThat(stringCalculator.add(text)).isZero();
    }

    @DisplayName(value = "숫자 하나를 문자열로 입력할 경우 해당 숫자를 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1"})
    void oneNumber(final String text) {
        assertThat(stringCalculator.add(text)).isSameAs(Integer.parseInt(text));
    }

    @DisplayName(value = "숫자 두개를 쉼표(,) 구분자로 입력할 경우 두 숫자의 합을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1,2"})
    void twoNumbers(final String text) {
        assertThat(stringCalculator.add(text)).isSameAs(3);
    }

    @DisplayName(value = "구분자를 쉼표(,) 이외에 콜론(:)을 사용할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"1,2:3"})
    void colons(final String text) {
        assertThat(stringCalculator.add(text)).isSameAs(6);
    }

    @DisplayName(value = "//와 \\n 문자 사이에 커스텀 구분자를 지정할 수 있다.")
    @ParameterizedTest
    @ValueSource(strings = {"//;\n1;2;3"})
    void customDelimiter(final String text) {
        assertThat(stringCalculator.add(text)).isSameAs(6);
    }

    @DisplayName(value = "문자열 계산기에 음수를 전달하는 경우 RuntimeException 예외 처리를 한다.")
    @Test
    void negative() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> stringCalculator.add("-1"));
    }


}