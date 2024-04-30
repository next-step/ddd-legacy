package racingcar;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CarTest {

    @DisplayName("자동차 이름은 5글자를 넘을 수 없다")
    @Test
    void constructor() {
        Assertions.assertThatIllegalArgumentException()
            .isThrownBy(() -> new Car("룰루랄라랄랄랄라"));
    }

    @DisplayName("숫자가 4 이상인 경우 자동차는 전진한다")
    @Test
    void move() {
        final Car car = new Car("황일용");
        car.move(4);
        assertThat(car.position()).isEqualTo(1);
    }

    @DisplayName("숫자가 3 이하인 경우 자동차는 멈춘다")
    @Test
    void stop() {
        final Car car = new Car("황일용");
        car.move(3);
        assertThat(car.position()).isEqualTo(0);
    }
}
