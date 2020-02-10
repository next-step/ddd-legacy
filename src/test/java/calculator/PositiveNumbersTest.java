package calculator;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PositiveNumbersTest {

    @Test
    public void construct_with_null() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new PositiveNumbers(null));
    }

    @MethodSource("sum_cases")
    @ParameterizedTest
    void sum(PositiveNumbers positiveNumbers,
             int expected) {
        assertThat(positiveNumbers.sum())
            .isEqualTo(expected);
    }

    private static Stream<Arguments> sum_cases() {
        return Stream.of(Arguments.of(positiveNumbersFrom(), 0),
                         Arguments.of(positiveNumbersFrom(1), 1),
                         Arguments.of(positiveNumbersFrom(0, 0), 0),
                         Arguments.of(positiveNumbersFrom(1, 2, 3), 6));
    }

    static PositiveNumbers positiveNumbersFrom(int... numbers) {
        return new PositiveNumbers(Arrays.stream(numbers)
                                         .mapToObj(PositiveNumber::new)
                                         .collect(toList()));
    }

}