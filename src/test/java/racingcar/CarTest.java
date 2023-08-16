package racingcar;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CarTest {
    @DisplayName("자동차는 이름을 가지고 있다")
    @Test
    void name() {
        final var actual = new Car("soos");
        assertThat(actual.getName()).isEqualTo("soos");
    }

    @DisplayName("자동차의 이름이 5글자를 넘으면 예외가 발생한다")
    @Test
    void max_length_of_name() {
        assertThatThrownBy(() -> new Car("동해물과백두산"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("자동차는 무작위 값이 4 이상인 경우 전진한다")
    @Test
    void move() {
        final var car = new Car("soos");
        car.move(new NumberMoveCondition(4));
        assertThat(car.getPosition()).isOne();
    }

    @DisplayName("자동차는 무작위 값이 3 이하인 경우 멈춰있다")
    @ValueSource(ints = {0, 1, 2, 3})
    @ParameterizedTest
    void dont_move(final int condition) {
        final var car = new Car("soos");
        car.move(new NumberMoveCondition(condition));
        assertThat(car.getPosition()).isZero();
    }

    @DisplayName("자동차는 정지한다")
    @Test
    void stop() {
        final var car = new Car("soos");
        car.move(new StopMoveCondition());
        assertThat(car.getPosition()).isZero();
    }
}
