package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InputParserTest {
    @ParameterizedTest
    @NullSource
    @EmptySource
    @DisplayName("null 또는 빈 문자열을 전달하는 경우 빈 리스트를 반환")
    void shouldBeZeroWhenParseEmptyString(String text) {
        assertThat(InputParser.parseToInts(text)).isEmpty();
    }

    @ParameterizedTest
    @MethodSource("provideDefaultDelimiterValues")
    @DisplayName(", 또는 :을 구분자로 가지는 문자열을 전달하는 경우 구분자를 기준으로 분리한 숫자의 리스트를 반환")
    void parseStringToIntegerListWithDefaultDelimiters(String value, List<Integer> expected) {
        assertThat(InputParser.parseToInts(value))
                .isEqualTo(expected);
    }

    private static Stream<Arguments> provideDefaultDelimiterValues() {
        return Stream.of(
                Arguments.of("1", Arrays.asList(1)),
                Arguments.of("1,2,3", Arrays.asList(1,2,3)),
                Arguments.of("1:2", Arrays.asList(1,2)),
                Arguments.of("1,2:3:4,5", Arrays.asList(1,2,3,4,5)),
                Arguments.of("-1,2:-3", Arrays.asList(-1,2,-3))
        );
    }

    @ParameterizedTest
    @MethodSource("provideWithCustomDelimiterValues")
    @DisplayName("문자열 앞부분의 “//”와 “\\n” 사이에 위치하는 문자를 커스텀 구분자로 가지는 문자열을 전달하는 경우 분리한 숫자의 리스트를 반환")
    void parseStringToIntegerListWithCustomDelimiters(String value, List<Integer> expected) {
        assertThat(InputParser.parseToInts(value))
                .isEqualTo(expected);
    }

    private static Stream<Arguments> provideWithCustomDelimiterValues() {
        return Stream.of(
                Arguments.of("//;\\n1;2;3", Arrays.asList(1,2,3)),
                Arguments.of("//@\\n4@5", Arrays.asList(4,5))
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidValues")
    @DisplayName("숫자 이외의 값을 전달하는 경우 RuntimeException 예외 던짐")
    void shouldThrowExceptionWhenCanNotParseNumber(String value) {
        assertThatThrownBy(() -> InputParser.parseToInts(value))
                .isInstanceOf(RuntimeException.class);
    }

    private static Stream<Arguments> provideInvalidValues() {
        return Stream.of(
                Arguments.of("1,b,3"),
                Arguments.of("c:5"),
                Arguments.of("6,d:8:9,f"),
                Arguments.of("//@\\na@5")
        );
    }
}
