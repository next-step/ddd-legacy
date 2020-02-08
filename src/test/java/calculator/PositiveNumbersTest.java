package calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

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
        return Stream.of(Arguments.of(PositiveNumbers.from(), 0),
                         Arguments.of(PositiveNumbers.from(1), 1),
                         Arguments.of(PositiveNumbers.from(0, 0), 0),
                         Arguments.of(PositiveNumbers.from(1, 2, 3), 6));
    }


}