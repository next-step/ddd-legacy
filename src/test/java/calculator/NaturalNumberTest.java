package calculator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class NaturalNumberTest {

    @DisplayName("자연수를 생성한다.")
    @Test
    void constructor() {
        new NaturalNumber("1");
    }

    @DisplayName("자연수가 아닌 수의 경우 예외를 던진다.")
    @ParameterizedTest
    @ValueSource(strings = {"-1", "0"})
    void throwOnNegative(String numberStr) {
        Assertions.assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> new NaturalNumber(numberStr));
    }
}