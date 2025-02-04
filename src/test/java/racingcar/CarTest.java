package racingcar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CarTest {

    @DisplayName("자동차 이름은 5 글자를 넘을 수 없다.")
    @Test
    void constructor() {
        String overFiveLengthName = "일이삼사오육";
        assertThatThrownBy(() -> {
            new Car(overFiveLengthName);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("자동차는 조건이 맞는 경우 움직인다.")
    @Test
    void move() {
        final Car car = new Car("test");
        car.move(new CarForwardStrategy());
        assertThat(car.position()).isEqualTo(1);
    }

    @DisplayName("자동차는 조건이 맞는 경우 정지한다.")
    @Test
    void stop() {
        final Car car = new Car("test");
        car.move(new CarStopStrategy());
        assertThat(car.position()).isEqualTo(0);
    }
}