package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class PositiveIntegerTest {

    @DisplayName(value = "PositiveInteger 에 자연수가 아닌 수를 입력하는 경우 RuntimeException 을 생성한다.")
    @ParameterizedTest
    @ValueSource(ints = {-1, 0})
    void inputNegativeOrZero() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> new PositiveInteger(-1));
    }

    @DisplayName(value = "Integer 범위를 넘어서는 값을 입력하면 RuntimeException을 생성한다.")
    @Test
    void overIntegerRange(){
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> new PositiveInteger(Integer.MAX_VALUE + 1));
    }
}
