package stringcalculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class StringCalculatorTokenParserTest {
    private StringCalculatorTokenParser tokenParser;

    @BeforeEach
    void setUp() {
        tokenParser = new StringCalculatorTokenParser();
    }

    @ParameterizedTest
    @ValueSource(strings = {"1,2,3", "//;\n1;2;3"})
    @DisplayName("기본 구분자 테스트")
    void testValidInputsWithDifferentDelimiters(String input) {
        List<NonNegativeInteger> result = tokenParser.getIntegerTokens(input);
        assertEquals(3, result.size());
        assertEquals(1, result.get(0).getInteger());
        assertEquals(2, result.get(1).getInteger());
        assertEquals(3, result.get(2).getInteger());
    }

    @ParameterizedTest
    @CsvSource({
            "'1,2,3', 3",
            "'//;\n1;2;3', 3",
            "'1,2:3', 3",
            "'//-\n1-2-3', 3",
    })
    @DisplayName("올바른 입력이 주어 졌을때 토큰 개수가 맞는지 확인")
    void testValidInputs(String input, int expectedSize) {
        List<NonNegativeInteger> result = tokenParser.getIntegerTokens(input);
        assertEquals(expectedSize, result.size());
    }


    @ParameterizedTest
    @ValueSource(strings = {"1,2,three", "1,-2,3", "", "//,\n"})
    @DisplayName("올바르지 않은 입력값이 주어지면 런타임에러가 발생한다.")
    void testInvalidAndNegativeNumbers(String input) {
        assertThrows(RuntimeException.class, () -> tokenParser.getIntegerTokens(input));
    }
}
