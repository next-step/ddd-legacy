package calculator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NumberTest {

    @Test
    @DisplayName("숫자를 생성한다")
    void createNumber() {
        Number number = new Number("20");
        assertThat(number.getNumber()).isEqualTo(20);
    }

    @Test
    @DisplayName("음수를 생성하는 경우 IllegalArgumentException이 발생한다")
    void createNumberException() {
        assertThatThrownBy(() -> {
            Number number = new Number("-20");
        }).isInstanceOf(IllegalArgumentException.class);
    }
}
