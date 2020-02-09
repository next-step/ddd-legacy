package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NumberTest {

    @DisplayName("음수 혹은 숫자 이외의 값일 때 RunTimeException 을 던짐")
    @ParameterizedTest
    @ValueSource(strings = {"a", "-7"})
    void addNegativeAndChar(String input) {
        assertThrows(RuntimeException.class, () -> new Number(input));
    }

    @Test
    @DisplayName("더하기 정상작동일 때")
    void sum() {
        Number number = new Number("0");
        assertThat(number.sum(new Number("2")).getValue()).isEqualTo(2);
    }
}

