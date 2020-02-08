package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NumberTest {

    @ParameterizedTest
    @ValueSource(strings = {"0", "1", "100"})
    @DisplayName("Number 생성")
    void createNumber(int value) {
        assertThat(Number.from(value)).isNotNull();
    }

    @Test
    @DisplayName("음수인 경우 Number 생성시 예외 던짐")
    void shouldThrowRuntimeExceptionWhenCreateWithNegativeNumber() {
        assertThatThrownBy(() -> Number.from(-1))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Number 더하기")
    void sumNumber() {
        Number first = Number.from(3);
        Number second = Number.from(10);

        Number sum = first.sum(second);

        assertThat(sum.getValue())
                .isEqualTo(first.getValue() + second.getValue());
        assertThat(sum).isNotEqualTo(first);
    }
}