package stringcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class PositiveNumberTest {

    @DisplayName("PositiveNumber 가 0 보다 작을 수 없다.")
    @Test
    void constructor_invalid() {
        assertThatThrownBy(() -> new PositiveNumber(-1)).isInstanceOf(RuntimeException.class);
    }

    @DisplayName("PositiveNumber 는 0 보다 크거나 같아야 가능하다.")
    @Test
    void constructor() {
        assertAll(() -> {
            assertThatCode(() -> new PositiveNumber(0)).doesNotThrowAnyException();
            assertThatCode(() -> new PositiveNumber(1)).doesNotThrowAnyException();
        });
    }
}
