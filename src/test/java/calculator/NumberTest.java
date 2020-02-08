package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertThrows;

class NumberTest {

    @DisplayName("음수 혹은 숫자 이외의 값일 때 RunTimeException 을 던짐")
    @ParameterizedTest
    @ValueSource(strings = {"a", "-7"})
    void addNegative(String input) {
        assertThrows(RuntimeException.class, () -> new Number(input));
    }
}
