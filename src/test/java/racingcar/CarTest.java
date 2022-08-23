package racingcar;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CarTest {

    @DisplayName("이동 조건이 충족될 경우 자동차가 이동한다.")
    @Test
    void move() {
        Car car = new Car("최현구", 0);
        car.move(() -> true);

        assertThat(car.getPositionValue()).isEqualTo(1);
    }

    @DisplayName("이동 조건이 충족되지 않을 경우 자동차가 이동하지 않는다.")
    @Test
    void stop() {
        Car car = new Car("최현구", 0);
        car.move(() -> false);

        assertThat(car.getPositionValue()).isEqualTo(0);
    }
}
