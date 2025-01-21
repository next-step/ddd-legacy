package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class CarTest {

    @DisplayName("자동차의 이름은 5글자를 초과하면 예외가 발생한다.")
    @Test
    void constructor() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Car("gugbab2"));
    }

    @Test
    @DisplayName("자동차는 움직인다.")
    void move() {
        Car car = new Car("bab2");
        car.move(new ForwardStrategy());
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @Test
    @DisplayName("자동차는 정지한다.")
    void stop() {
        Car car = new Car("bab2");
        car.move(new StopStrategy());
        assertThat(car.getPosition()).isEqualTo(0);
    }
}