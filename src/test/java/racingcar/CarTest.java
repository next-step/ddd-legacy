package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CarTest {
    @DisplayName("자동차는 이름을 가지고 있다")
    @Test
    void name() {
        final var actual = new Car("jason");
        assertThat(actual.getName()).isEqualTo("jason");
    }

    @DisplayName("자동차의 이름이 5글자를 넘으면 예외가 발생한다")
    @Test
    void invalid_name() {
        assertThatThrownBy(() -> new Car("동해물과백두산이"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("자동차는 무작위 값이 4 이상인 경우 전진한다")
    @Test
    void move() {
        final Car car = new Car("jason");
        car.move(new NumberMoveCondition(4));
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("자동차는 무작위 값이 3 이하인 경우 정지한다")
    @ValueSource(ints = {0, 1, 2, 3})
    @ParameterizedTest
    void stop(final int condition) {
        final Car car = new Car("jason");
        car.move(new NumberMoveCondition(condition));
        assertThat(car.getPosition()).isZero();
    }

    @DisplayName("자동차는 정지한다")
    @Test
    void stop() {
        final Car car = new Car("jason");
        car.move(new StopMoveCondition());
        assertThat(car.getPosition()).isZero();
    }
}
