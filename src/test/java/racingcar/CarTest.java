package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class CarTest {

    @DisplayName("자동차 이름은 5 글자를 넘을 수 없다.")
    @Test
    void carName() {
        assertThatThrownBy(() -> new Car("abcdefghij"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("자동차가 움직인다")
    @Test
    void move() {
        Car car = new Car("car1", 0);
        car.move(new ForwardStrategy());
        assertThat(car.getPosition()).isOne();
    }

    @DisplayName("자동차가 움직이지 않는다")
    @Test
    void stop() {
        Car car = new Car("car2", 0);
        car.move(new HoldStrategy());
        assertThat(car.getPosition()).isZero();
    }
}
