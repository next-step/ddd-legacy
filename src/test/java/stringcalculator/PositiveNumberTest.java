package stringcalculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PositiveNumberTest {

    @Test
    @DisplayName("PositiveNumber 는 음수가 될 수 없다. 음수인 경우")
    void negative() {
        assertThatThrownBy(() -> new PositiveNumber(-1))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("PositiveNumber 는 음수가 될 수 없다. 양수인 경우")
    void positive() {
        assertDoesNotThrow(() -> new PositiveNumber(1));
    }

    @Test
    void plus() {
        final PositiveNumber one = new PositiveNumber(1);
        final PositiveNumber two = new PositiveNumber(2);

        final PositiveNumber three = new PositiveNumber(3);

        assertThat(one.plus(two)).isEqualTo(three);
    }
}
