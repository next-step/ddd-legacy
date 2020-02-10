package calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PositiveNumberTest {

    @ValueSource(strings = { "-1", "1a", "a", })
    @ParameterizedTest
    void from_when_text_contain_not_only_positive_number(String notOnlyPositiveNumber) {
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> PositiveNumber.from(notOnlyPositiveNumber));
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