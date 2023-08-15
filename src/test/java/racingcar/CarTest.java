package racingcar;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CarTest {

    @DisplayName("자동차의 이름을 가지고 있다")
    @Test
    void name() {
        Car car = new Car("lozm");
        Assertions.assertThat(car.getName()).isEqualTo("lozm");
    }

    @DisplayName("자동차의 이름이 5글자를 넘으면 예외가 발생한다")
    @Test
    void invalid_name() {
        Assertions.assertThatThrownBy(() -> new Car("lozmlozmlozm"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("자동차는 무작위 값이 4 이상인 경우 전진한다")
    @Test
    void move() {
        Car car = new Car("lozm");
        car.move(new NumberMoveCondition(4));
        Assertions.assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("자동차는 무작위 값이 3 이하인 경우 정지한다")
    @ValueSource(ints = {0, 1, 2, 3})
    @ParameterizedTest
    void stop(final int condition) {
        Car car = new Car("lozm");
        car.move(new NumberMoveCondition(condition));
        Assertions.assertThat(car.getPosition()).isZero();
    }

    @DisplayName("자동차는 정지한다")
    @Test
    void stop() {
        Car car = new Car("lozm");
        car.move(new StopMoveCondition());
        Assertions.assertThat(car.getPosition()).isZero();
    }

}
