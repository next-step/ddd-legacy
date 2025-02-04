package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

class CarTest {
    @DisplayName("자동차의 이름 5글자 초과시 예외 발생")
    @Test
    void constructor() {
        assertThatIllegalArgumentException().isThrownBy(() -> {
            new Car("thisisname");
        });
    }

    @DisplayName("자동차 움직임")
    @Test
    void move() {
        Car car = new Car("car");
        car.move(new ForwardStrategy());
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("자동차가 정지")
    @Test
    void stop() {
        Car car = new Car("car");
        car.move(() -> false);
        assertThat(car.getPosition()).isEqualTo(0);
    }

    static class ForwardStrategy implements MovingStrategy {
        @Override
        public boolean movable() {
            return true;
        }
    }

}