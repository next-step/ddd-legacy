package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PositiveNumberTest {

    @ParameterizedTest
    @ValueSource(strings = {"0", "1", "100"})
    @DisplayName("PositiveNumber 생성")
    void createNumber(int value) {
        assertThat(PositiveNumber.from(value)).isNotNull();
    }

    @Test
    @DisplayName("음수인 경우 PositiveNumber 생성시 예외 던짐")
    void shouldThrowRuntimeExceptionWhenCreateWithNegativeNumber() {
        assertThatThrownBy(() -> PositiveNumber.from(-1))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("PositiveNumber 더하기")
    void sumNumber() {
        PositiveNumber first = PositiveNumber.from(3);
        PositiveNumber second = PositiveNumber.from(10);

        PositiveNumber sum = first.sum(second);

        assertThat(sum.getValue())
                .isEqualTo(first.getValue() + second.getValue());
        assertThat(sum).isNotEqualTo(first);
    }
}