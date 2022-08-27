package calculator.verifier;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PositiveNumberVerifierTest {

    private PositiveNumberVerifier positiveNumberVerifier = new PositiveNumberVerifier();

    private static Stream<Arguments> providePositiveNumberExpressions() {
        return Stream.of(
            Arguments.of(Arrays.asList("1", "3", "123", "32")),
            Arguments.of(Arrays.asList("134543", "0"))
        );
    }

    private static Stream<Arguments> provideCharacterExpressions() {
        return Stream.of(
            Arguments.of(Arrays.asList("1", "g", "23", "0")),
            Arguments.of(Arrays.asList("1", "3", "23", "a"))
        );
    }

    private static Stream<Arguments> providePositiveExpressions() {
        return Stream.of(
            Arguments.of(Arrays.asList("1", "-123", "23", "0")),
            Arguments.of(Arrays.asList("1", "23", "-3"))
        );
    }

    @DisplayName("양수만 존재하는 문자열이라면 정상 동작")
    @ParameterizedTest
    @MethodSource("providePositiveNumberExpressions")
    void verify(final List<String> expressions) {
        assertThatCode(() -> positiveNumberVerifier.verify(expressions));
    }

    @DisplayName("문자가 존재한다면 RuntimeException 발생")
    @ParameterizedTest
    @MethodSource("provideCharacterExpressions")
    void contains_character(final List<String> expressions) {
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> positiveNumberVerifier.verify(expressions));
    }

    @DisplayName("0보다 작은 수가 존재한다면 RuntimeException 발생")
    @ParameterizedTest
    @MethodSource("providePositiveExpressions")
    void contains_negative_number(final List<String> expressions) {
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> positiveNumberVerifier.verify(expressions));
    }
}
