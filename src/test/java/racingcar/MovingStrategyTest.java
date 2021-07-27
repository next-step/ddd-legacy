package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class MovingStrategyTest {

    @DisplayName("값이 4 이상인 경우, 움직일 수 있다.")
    @ParameterizedTest(name = "값: {0}")
    @ValueSource(ints = {4, 5, 6, 7, 8, 9})
    void movable(int value) {
        assertThat(MovingStrategy.isMovable(value))
                .isTrue();
    }

    @DisplayName("값이 4 미만인 경우, 움직일 수 없다.")
    @ParameterizedTest(name = "값: {0}")
    @ValueSource(ints = {0, 1, 2, 3})
    void notMovable(int value) {
        assertThat(MovingStrategy.isMovable(value))
                .isFalse();
    }
}
