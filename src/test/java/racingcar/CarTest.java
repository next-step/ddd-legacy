package racingcar;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CarTest {

    @Test
    @DisplayName("자동차 이름은 5글자를 넘을 수 없다.")
    void constructor() {
        Assertions.assertThatThrownBy(() -> new Car("BMW_X3"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("숫자가 4 이상인 경우 자동차는 전진한다.")
    void move() {
        final var car = new Car("TEST1");
        car.move(4);
        Assertions.assertThat(car.position()).isEqualTo(1);
    }
    @Test
    @DisplayName("숫자가 4 미만인 경우 자동차는 움직이지 않는다.")
    void stop() {
        final var car = new Car("TEST2");
        car.move(3);
        Assertions.assertThat(car.position()).isEqualTo(0);
    }
}
