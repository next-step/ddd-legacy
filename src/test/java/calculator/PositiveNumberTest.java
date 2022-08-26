package calculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

public class PositiveNumberTest {

    @DisplayName("객체 값 검증")
    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    void success(int value) {
        // when
        PositiveNumber positiveNumber = new PositiveNumber(value);

        // then
        assertThat(positiveNumber.getValue()).isEqualTo(value);
    }

    @DisplayName("예외, 숫자 이외의 값")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"a"})
    void error1(String actual) {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new PositiveNumber(actual));
    }

    @DisplayName("예외, 0 또는 음수")
    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void error2(int value) {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new PositiveNumber(value));
    }
}
