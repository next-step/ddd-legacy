package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class CarTest {

    @DisplayName("자동차의 이름이 5자리가 넘지 않으면 정상처리 된다.")
    @Test
    void carName() {
        String name = "손진영";
        Car car = new Car(name);
        assertThat(car.getName()).isEqualTo(name);
    }

    @DisplayName("자동차의 이름이 5자리가 넘으면 예외가 발생한다.")
    @Test
    void invalidCarName() {
        String name = "son jin young";
        assertThatThrownBy(() -> new Car(name))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("무작위 값이 4 이상인 경우 자동차는 움직인다.")
    @Test
    void carMovable() {
        Car car = new Car("손진영");
        car.move(new CarMoveCondition(4));
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("무작위 값이 4 이하인 경우 자동차는 움직이지 않는다.")
    @Test
    void carCannotMove() {
        Car car = new Car("손진영");
        car.move(new CarMoveCondition(3));
        assertThat(car.getPosition()).isEqualTo(0);
    }
}
