package racingcar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CarTest {

    @Test
    @DisplayName("자동차 이름은 5글자를 넘을 수 없다.")
    void throwsExceptionWhenCreateCar() {
        assertThatThrownBy(() -> {
            Car car = new Car("가나다라마바");
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("자동차는 무작위 값이 4이상인 경우 움직인다.")
    void moving() {
        Car car = new Car("가나다라마");
        assertThat(car.movable()).isTrue();
    }
}
