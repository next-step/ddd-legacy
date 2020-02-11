package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class PositiveNumberTest {

    @DisplayName("0을 포함한 자연수 문자열로 생성시 성공해야함")
    @ParameterizedTest
    @MethodSource("createStringPositiveNumber")
    void stringPositiveNumber(String text, int expectedResult) {
        int result = PositiveNumber.of(text).toInt();

        assertThat(result).isEqualTo(expectedResult);
    }

    private static Stream<Arguments> createStringPositiveNumber() {
        return Stream.of(
                Arguments.of("0", 0),
                Arguments.of("11", 11));
    }

    @DisplayName("0을 포함한 자연수로 생성시 성공해야함")
    @ParameterizedTest
    @MethodSource("createIntPositiveNumber")
    void intPositiveNumber(int number, int expectedResult) {
        int result = PositiveNumber.of(number).toInt();

        assertThat(result).isEqualTo(expectedResult);
    }

    private static Stream<Arguments> createIntPositiveNumber() {
        return Stream.of(
                Arguments.of(0, 0),
                Arguments.of(22, 22));
    }

    @DisplayName("음수에서는 예외 발생")
    @Test
    void stringNegative() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> PositiveNumber.of("-1"));
    }

    @DisplayName("음수에서는 예외 발생")
    @Test
    void intNegative() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> PositiveNumber.of(-1));
    }

}
