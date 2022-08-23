package racingcar;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CarPositionTest {

    @DisplayName("자동차 포지션은 0 이상이다.")
    @Test
    void position() {
        assertThatCode(() -> new CarPosition(0))
            .doesNotThrowAnyException();
    }

    @DisplayName("자동차 포지션은 음수일 수 없다.")
    @Test
    void negativeException() {
        assertThatExceptionOfType(IllegalArgumentException.class)
            .isThrownBy(() -> new CarPosition(-1));
    }
}
