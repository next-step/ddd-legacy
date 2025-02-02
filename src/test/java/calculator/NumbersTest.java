package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class NumbersTest {

    @DisplayName(value = "초기화")
    @ParameterizedTest
    @MethodSource("provideStringLists")
    void constructor(List<String> input) {
        Numbers numbers = new Numbers(input);
        assertThat(numbers.getNumbers()).isEqualTo(Arrays.asList(1, 2, 3));
    }

    @DisplayName(value = "null or empty 확인")
    @Test
    void nullOrEmpty() {
        Numbers numbers = new Numbers();
        assertThat(numbers.isNullOrEmpty()).isTrue();
    }

    @DisplayName(value = "문자열 > 숫자로 변환")
    @ParameterizedTest
    @ValueSource(strings = {"1"})
    void convertNumber(final String input) {
        assertThat(Numbers.convertNumber(input)).isEqualTo(1);
    }

    @DisplayName(value = "음수 입력 시, RuntimeException 예외 처리")
    @ParameterizedTest
    @MethodSource("provideStringLists2")
    void negativeNumber(List<String> input) {
        Numbers numbers = new Numbers(input);
        assertThat(numbers.hasNegativeNumber()).isTrue();
    }

    @DisplayName(value = "numbers 전체 숫자 합산")
    @ParameterizedTest
    @MethodSource("provideStringLists")
    void sum(List<String> input) {
        Numbers numbers = new Numbers(input);
        assertThat(numbers.sum()).isEqualTo(provideStringLists()
                .flatMap(List::stream)
                .mapToInt(Integer::parseInt)
                .sum());
    }

    @DisplayName(value = "숫자 외 입력 시, RuntimeException 예외 처리")
    @ParameterizedTest
    @ValueSource(strings = {"ABC"})
    void invalidNumber(final String input) {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> Numbers.convertNumber(input));
    }

    static Stream<List<String>> provideStringLists() {
        return Stream.of(
                Arrays.asList("1", "2", "3")
        );
    }

    static Stream<List<String>> provideStringLists2() {
        return Stream.of(
                Arrays.asList("1", "2", "-3")
        );
    }

}
