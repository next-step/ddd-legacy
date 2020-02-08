package calculator;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntUnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.platform.commons.util.StringUtils;

class StringParserTest {

    private StringParser stringParser;

    @BeforeEach
    void setUp() {
        stringParser = new StringParser();
    }

    @DisplayName("empty string 입력시에도 empty list를 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"", "  ", " "})
    void customSplit(final String input) {
        assertThat(this.stringParser.split(input)).isEmpty();
    }

    @DisplayName("null 입력시에도 empty list를 반환한다.")
    @Test
    void customSplit() {
        assertThat(this.stringParser.split(null).size()).isZero();
    }

    @DisplayName("입력값에 separator 미 포함시 기본 separator로 split한다.")
    @ParameterizedTest
    @MethodSource("defaultSeparatorArgs")
    void defaultSeparator(String input, List<PositiveNumber> result) {
        assertThat(this.stringParser.split(input)).containsAll(result);
    }

    static Stream<Arguments> defaultSeparatorArgs() {
        return Stream.of(
            Arguments.of("0:0:1", newInstances(0, 0, 1)),
            Arguments.of("1,2:3", newInstances(1, 2, 3)),
            Arguments.of("1,2,4", newInstances(1, 2, 4))
        );
    }

    @DisplayName("입력값에 커스텀 separator로 split한다.")
    @ParameterizedTest
    @MethodSource("customSeparatorArgs")
    void customSeparator(String input, List<PositiveNumber> result) {
        assertThat(this.stringParser.split(input)).containsAll(result);
    }

    static Stream<Arguments> customSeparatorArgs() {
        return Stream.of(
            Arguments.of("//a\n0a0a1", newInstances(0, 0, 1)),
            Arguments.of("//a\n1a2a3", newInstances(1, 2, 3)),
            Arguments.of("//b\n1b2b4", newInstances(1, 2, 4))
        );
    }

    static List<PositiveNumber> newInstances(Integer... numbers) {
        return Stream.of(numbers)
            .map(PositiveNumber::new)
            .collect(Collectors.toList());
    }
}
