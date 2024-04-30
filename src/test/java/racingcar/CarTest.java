package racingcar;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CarTest {
    @Test
    @DisplayName("자동차 이름은 5 글자를 넘을 수 없다.")
    void nameException() {
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> new Car("아아아아아아"));
    }

    @Test
    @DisplayName("4 이상인 경우 자동차는 움직인다.")
    void move() {
        Car car = new Car("아아아아");
        car.move(new GoStrategy());
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @Test
    @DisplayName("4 미만인 경우 움직이지 않는다.")
    void stop() {
        Car car = new Car("아아아아");
        car.move(new StopStrategy());
        assertThat(car.getPosition()).isEqualTo(0);
    }
}
