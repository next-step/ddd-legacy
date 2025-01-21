package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CarTest {

    @Test
    @DisplayName("자동차 이름은 5 글자를 넘을 수 없다.")
    void createCar() {
        // given
        final String name = "abcdef";

        // when & then
        assertThrows(IllegalArgumentException.class, () -> new Car(name));
    }

    @Test
    @DisplayName("자동차가 전진하는지 확인한다.")
    void move() {
        // given
        final Car car = new Car("car");

        // when
        car.move(new ForwardStrategy());

        // then
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @Test
    @DisplayName("자동차가 정지하는지 확인한다.")
    void stop() {
        // given
        final Car car = new Car("car");

        // when
        car.move(new StopStrategy());

        // then
        assertThat(car.getPosition()).isEqualTo(0);
    }

}
