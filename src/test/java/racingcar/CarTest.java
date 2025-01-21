package racingcar;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;


class CarTest {


    @DisplayName("Car의 name은 5글자를 넘으면 예외가 발생한다")
    @Test
    void constructor() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Car("abcdef"));
    }

    @DisplayName("자동차는 움직인다")
    @Test
    void move() {
        final var car = new Car("jaeg");
        car.move(() -> true);
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("자동차는 정지한다")
    @Test
    void stop() {
        final var car = new Car("jaeg");
        car.move(new StopStrategy());
        assertThat(car.getPosition()).isEqualTo(0);
    }


}