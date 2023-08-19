package stringcalculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.*;

public class NumberTest {
    @DisplayName("Number 생성")
    @Test
    void create() {
        Number result = Number.of(1);
        assertThat(result.getValue()).isEqualTo(1);
    }

    @DisplayName("음수가 입력될 시 예외가 발생한다.")
    @Test
    void input_negative_throw_exception() {
        assertThatThrownBy(() -> Number.of(-1))
                .isInstanceOf(RuntimeException.class);
    }
}
