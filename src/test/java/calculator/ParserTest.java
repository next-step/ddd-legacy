package calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ParserTest {
    private Parser parser;

    @BeforeEach
    void setup() {
        parser = new Parser();
    }

    @DisplayName("구분자를 주지 않았을 때 기본 구분자를 사용한다")
    @ParameterizedTest
    @MethodSource("createTextExcludeDelimiter")
    void useDefaultDelimiter(String text, String[] expectedResult) {
        String[] result = parser.parseStrings(text);

        assertThat(result).isEqualTo(expectedResult);
    }

    private static Stream<Arguments> createTextExcludeDelimiter() {
        return Stream.of(
                Arguments.of("1,2:3", new String[]{"1", "2", "3"}),
                Arguments.of("4:5,6", new String[]{"4", "5", "6"}));
    }

    @DisplayName("구분자를 주었을때는 주어진 구분자를 사용한다")
    @ParameterizedTest
    @MethodSource("createTextIncludeDelimiter")
    void useCustomDelimiter(String text, String[] expectedResult) {
        String[] result = parser.parseStrings(text);

        assertThat(result).isEqualTo(expectedResult);
    }

    private static Stream<Arguments> createTextIncludeDelimiter() {
        return Stream.of(
                Arguments.of("//!\n1!2!3", new String[]{"1", "2", "3"}),
                Arguments.of("//!!\n1!!2!!3", new String[]{"1", "2", "3"}));
    }

}
