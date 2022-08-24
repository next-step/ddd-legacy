package calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class PositiveNumberTest {
    @DisplayName("숫자 이외의 값일 경우 예외 발생")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "a")
    void notNumber(String value) {
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> new PositiveNumber(value));
    }

    @DisplayName("양의 정수가 아닐 경우 예외 발생")
    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void notPositiveNumber(int value) {
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() -> new PositiveNumber(value));
    }

    @DisplayName("객체 값 검증")
    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    void create(int value) {
        // when
        PositiveNumber positiveNumber = new PositiveNumber(value);

        // then
        assertThat(positiveNumber.getValue()).isEqualTo(value);
    }
}
