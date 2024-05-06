package stringcalculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import stringcalculator.parser.StringCalculatorParser;

class StringCalculatorParserTest {
    private StringCalculatorParser stringCalculatorParser;

    @BeforeEach
    void setUp() {
        stringCalculatorParser = new StringCalculatorParser();
    }

    @DisplayName("구분자가 지정되지 않은 경우 테스트")
    @ParameterizedTest
    @MethodSource("testDefaultSource")
    void testDefault(String expression, List<Integer> expected) {
        List<Integer> result = stringCalculatorParser.execute(expression).getIntegers();
        assertThat(result).containsExactlyElementsOf(expected);
    }

    @DisplayName("구분자가 지정된 경우 테스트")
    @ParameterizedTest
    @MethodSource("testCustomSource")
    void testCustom(String expression, List<Integer> expected) {
        List<Integer> result = stringCalculatorParser.execute(expression).getIntegers();
        assertThat(result).containsExactlyElementsOf(expected);
    }

    @DisplayName("올바르지 않은 입력 파싱 실패 테스트")
    @Test
    void testFail() {
        String expression = "aaa";
        assertThatExceptionOfType(RuntimeException.class)
                               .isThrownBy(() -> stringCalculatorParser.execute(expression));
    }

    static Stream<Arguments> testCustomSource() {
        return Stream.of(
            Arguments.of("//;\n//#\n1;2;3", Arrays.asList(1,2,3)),
            Arguments.of("//;\n//#\n-1;2#3", Arrays.asList(-1,2,3)),
            Arguments.of("//;\n//#\n1#2#3", Arrays.asList(1,2,3)),
            Arguments.of("//;\n1;2;3", Arrays.asList(1,2,3))
        );
    }

    static  Stream<Arguments> testDefaultSource() {
        return Stream.of(
            Arguments.of("1,2:3", Arrays.asList(1,2,3)),
            Arguments.of("1,2,3", Arrays.asList(1,2,3)),
            Arguments.of("1:2:3", Arrays.asList(1,2,3))
        );
    }
}
