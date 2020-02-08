package calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PositiveNumberTest {

    @MethodSource("from_when_text_contain_not_only_positive_number")
    @ParameterizedTest
    void from_when_text_contain_not_only_positive_number(String notOnlyPositiveNumber,
                                                         Class<Throwable> expected) {
        assertThatExceptionOfType(expected)
            .isThrownBy(() -> PositiveNumber.from(notOnlyPositiveNumber));
    }

    private static Stream<Arguments> from_when_text_contain_not_only_positive_number() {
        return Stream.of(Arguments.of("-1", RuntimeException.class),
                         Arguments.of("1a", RuntimeException.class),
                         Arguments.of("a", RuntimeException.class));
    }

    @Test
    void sum() {
        assertThat(PositiveNumber.ZERO.sum(new PositiveNumber(1)))
            .isEqualTo(new PositiveNumber(1));
    }

    @Test
    void sum_when_positiveNumber_is_null() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> PositiveNumber.ZERO.sum(null));
    }
}