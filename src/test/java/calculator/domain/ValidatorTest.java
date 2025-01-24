package calculator.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class ValidatorTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = new Validator();
    }

    @DisplayName("입력이 null 또는 빈 문자열일 경우 true를 반환해야 한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void isEmpty_shouldReturnTrueForNullOrEmpty(final String input) {
        assertThat(validator.isEmpty(input)).isTrue();
    }

    @DisplayName("입력에 텍스트가 포함되어 있으면 false를 반환해야 한다.")
    @ParameterizedTest
    @ValueSource(strings = {"123", "hello", "  a  "})
    void isEmpty_shouldReturnFalseForNonEmptyStrings(final String input) {
        assertThat(validator.isEmpty(input)).isFalse();
    }

    @DisplayName("음수가 포함된 경우 RuntimeException이 발생해야 한다.")
    @ParameterizedTest
    @MethodSource("provideNegativeNumbers")
    void assertPositiveNumbers_shouldThrowExceptionForNegativeNumbers(List<Integer> numbers) {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> validator.assertPositiveNumbers(numbers))
                .withMessageContaining("음수는 입력할 수 없습니다.");
    }

    @DisplayName("모든 숫자가 양수이면 예외가 발생하지 않아야 한다.")
    @ParameterizedTest
    @MethodSource("providePositiveNumbers")
    void assertPositiveNumbers_shouldNotThrowForPositiveNumbers(List<Integer> numbers) {
        assertThatCode(() -> validator.assertPositiveNumbers(numbers))
                .doesNotThrowAnyException();
    }

    private static Stream<List<Integer>> provideNegativeNumbers() {
        return Stream.of(
                Arrays.asList(1, -1, 3),
                Arrays.asList(2, -5, 4),
                Arrays.asList(10, -10, 20)
        );
    }

    private static Stream<List<Integer>> providePositiveNumbers() {
        return Stream.of(
                Arrays.asList(1, 2, 3),
                Arrays.asList(10, 20, 30),
                Arrays.asList(100, 200, 300)
        );
    }
}