package racingcar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CarTest {

    @Test
    @DisplayName("자동차 이름은 5글자를 넘을 수 없다")
    void constructor() {
        assertThatThrownBy(() -> new Car("다섯글자넘는")).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("자동차가 전진한다")
    void move() {
        Car car = new Car("다섯글자야");
        car.move(new ForwardMovingStrategy());

        assertThat(car.getPosition()).isEqualTo(1);
    }

    @Test
    @DisplayName("자동차가 움직이지 않는다")
    void hold() {
        Car car = new Car("다섯글자야");
        car.move(new HoldStrategy());

        assertThat(car.getPosition()).isZero();
    }
}
