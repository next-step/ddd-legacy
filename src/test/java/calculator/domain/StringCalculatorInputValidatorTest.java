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

import static calculator.domain.CalculatorException.*;
import static org.assertj.core.api.Assertions.*;

class StringCalculatorInputValidatorTest {

    private StringCalculatorInputValidator sut;
    private StringCalculatorDelimiters delimeters;

    @BeforeEach
    void setUp() {
        delimeters = StringCalculatorDelimiters.create();
        sut = new StringCalculatorInputValidator(delimeters);
    }

    @DisplayName("입력이 null 또는 빈 문자열일 경우 true를 반환해야 한다.")
    @ParameterizedTest
    @NullAndEmptySource
    void isEmpty_shouldReturnTrueForNullOrEmpty(final String input) {
        assertThat(sut.isEmpty(input)).isTrue();
    }

    @DisplayName("입력에 텍스트가 포함되어 있으면 false를 반환해야 한다.")
    @ParameterizedTest
    @ValueSource(strings = {"123", "hello", "  a  "})
    void isEmpty_shouldReturnFalseForNonEmptyStrings(final String input) {
        assertThat(sut.isEmpty(input)).isFalse();
    }

    @DisplayName("음수가 포함된 경우 RuntimeException이 발생해야 한다.")
    @ParameterizedTest
    @MethodSource("provideNegativeNumbers")
    void assertPositiveNumbers_shouldThrowExceptionForNegativeNumbers(List<Integer> numbers) {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> sut.assertPositiveNumbers(numbers))
                .withMessageContaining("음수는 입력할 수 없습니다.");
    }

    @DisplayName("모든 숫자가 양수이면 예외가 발생하지 않아야 한다.")
    @ParameterizedTest
    @MethodSource("providePositiveNumbers")
    void assertPositiveNumbers_shouldNotThrowForPositiveNumbers(List<Integer> numbers) {
        assertThatCode(() -> sut.assertPositiveNumbers(numbers))
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

    @DisplayName("올바른 입력값은 예외 없이 통과해야 한다.")
    @ParameterizedTest
    @MethodSource("validInputs")
    void shouldPassForValidInput(String input) {
        assertThatCode(() -> sut.assertValidInput(input))
                .doesNotThrowAnyException();
    }

    static Stream<String> validInputs() {
        return Stream.of(
                "", //빈 문자열 통과
                "1", "2", "100", // 숫자 하나
                "1,2,3", "10:20:30",// 기본 구분자
                "//;\n1;2;3"// 커스텀 구분자
        );
    }

    @DisplayName("잘못된 입력값은 예외를 발생시켜야 한다.")
    @ParameterizedTest
    @MethodSource("invalidInputs")
    void shouldThrowExceptionForInvalidInput(String input) {
        assertThatExceptionOfType(InvalidInputException.class)
                .isThrownBy(() -> sut.assertValidInput(input))
                .withMessageContaining("잘못된 입력 형식");
    }

    static Stream<String> invalidInputs() {
        return Stream.of(
                "1-2-3", // 등록 안된 구분자 사용
                "1a,2,3", // 숫자가 아닌 값 포함
                "abc", // 숫자가 전혀 없음
                "//;\n1;a;3", // 커스텀 구분자지만 숫자가 아닌 값 포함
                "1,,2", "2;;3", "3::4" // 연속된 구분자 (잘못된 입력)
        );
    }
}