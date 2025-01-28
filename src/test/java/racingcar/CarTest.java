package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class CarTest {

    @Test
    @DisplayName("자동차 이름을 반환한다")
    void getName() {
        Car car = new Car("test");
        assertThat(car.getName()).isEqualTo("test");
    }

    @Test
    @DisplayName("자동차 위치를 반환한다")
    void getPosition() {
        Car car = new Car("test");
        assertThat(car.getPosition()).isEqualTo(0);
    }

    @Test
    @DisplayName("자동차가 움직인다")
    void move() {
        Car car = new Car("test");
        car.move();
        assertThat(car.getPosition()).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("자동차 이름이 5 글자를 넘으면 IllegalArgumentException이 발생한다")
    void carNameExceedsMaxLength() {
        assertThatThrownBy(() -> new Car("toolongname"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Car name cannot be longer than 5 characters");
    }

    @Test
    @DisplayName("자동차가 무작위 값이 4 이상인 경우에만 움직인다")
    void carMovesBasedOnRandomValue() {
        Car car = new Car("test");
        for (int i = 0; i < 100; i++) {
            car.move();
        }
        assertThat(car.getPosition()).isBetween(0, 100);
    }
}