package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;


class CarTest {
    @DisplayName("자동차 이름은 5자 이하여야 합니다.")
    @Test
    void constructor() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Car("자동차이름다섯글자"));
    }

    @DisplayName("숫자가 4 미만일때 정지한다.")
    @Test
    void stop() {
        Car car = new Car("자동차");
        car.move(new StopStrategy());
        assertThat(car.getPosition()).isEqualTo(0);
    }

    @DisplayName("숫자가 4이상일때 움직인다.")
    @Test
    void move() {
        Car car = new Car("자동차");
        car.move(new ForwardStrategy());
        assertThat(car.getPosition()).isEqualTo(1);
    }
}
