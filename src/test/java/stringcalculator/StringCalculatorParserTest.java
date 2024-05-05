package stringcalculator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class StringCalculatorParserTest {
    private StringCalculatorParser stringCalculatorParser;

    @BeforeEach
    void setUp() {
        stringCalculatorParser = new StringCalculatorParser();
    }

    @ParameterizedTest
    @MethodSource("testDefaultSource")
    void testDefault(String expression, List<Integer> expected) {
        System.out.println(expression);
        System.out.println(expected);
        List<Integer> result = stringCalculatorParser.execute(expression);
        Assertions.assertThat(result).containsExactlyElementsOf(expected);
    }

    @ParameterizedTest
    @MethodSource("testCustomSource")
    void testCustom(String expression, List<Integer> expected) {
        List<Integer> result = stringCalculatorParser.execute(expression);
        Assertions.assertThat(result).containsExactlyElementsOf(expected);
    }

    @Test
    void testFail() {
        String expression = "aaa";
        AssertionsForClassTypes.assertThatExceptionOfType(RuntimeException.class)
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
