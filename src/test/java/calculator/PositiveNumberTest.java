package calculator;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PositiveNumberTest {
    @DisplayName("음수를 생성자에 전달할 경우 RuntimeException이 발생한다.")
    @Test
    void constructor() {
        assertThrows(RuntimeException.class, () -> {
            new PositiveNumber(-1);
        });
    }
}