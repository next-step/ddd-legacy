package racingcar;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import static org.assertj.core.api.Assertions.*;

class CarTest {

    @Test
    @DisplayName("자동차 이름은 5자를 넘길수 없다.")
    void constructor(){
        assertThatIllegalArgumentException().isThrownBy(() -> new Car("nameiscar"));
    }

    @Test
    @DisplayName("자동차는 움직인다.")
    void move() {
        Car car = new Car("car1");
        car.move(new ForwardStrategy());
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @Test
    @DisplayName("자동차는 정지한다.")
    void stop() {
        Car car = new Car("car1");
        car.move(new StopStrategy());
        assertThat(car.getPosition()).isEqualTo(0);
    }
}