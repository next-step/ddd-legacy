package racingcar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


class CarTest {

    @DisplayName("자동차의 이름은 5 글자를 넘을 수 없다.")
    @Test
    void constructor() {
        assertThatThrownBy(() -> new Car("다섯글자넘는이름"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("자동차가 전진한다.")
    @Test
    void forward() {
        Car car = new Car("race", 0);

        car.move(new ForwardStrategy());

        assertThat(car.position()).isEqualTo(1);
    }

    @DisplayName("자동차가 전진하지 않는다.")
    @Test
    void hold() {
        Car car = new Car("race", 0);

        car.move(new HoldStrategy());

        assertThat(car.position()).isZero();
    }

}