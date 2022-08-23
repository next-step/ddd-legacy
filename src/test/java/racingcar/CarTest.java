package racingcar;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


/*
 * 자동차 이름은 5 글자를 넘을 수 없다.
 * 5 글자가 넘는 경우, IllegalArgumentException이 발생한다.
 * 자동차가 움직이는 조건은 0에서 9 사이의 무작위 값을 구한 후, 무작위 값이 4 이상인 경우이다.
 * */
class CarTest {

    @DisplayName("자동차 이름은 5글자 이하이다.")
    @Test
    void constructCar() {
        Assertions.assertThatCode(() -> new Car("name", 0)).doesNotThrowAnyException();
    }

    @DisplayName("자동차 이름은 5 글자를 넘을 수 없다.")
    @Test
    void constructCarFail() {
        Assertions.assertThatThrownBy(() -> new Car("hellooo", 0)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("자동차 이름은 비어있을 수 없다.")
    @NullAndEmptySource
    @ParameterizedTest
    void constructCarNullAndEmpty(final String name) {
        Assertions.assertThatThrownBy(() -> new Car(name, 0)).isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("자동차가 움직일 수 있는 경우 이동한다.")
    @Test
    void move() {
        Car car = new Car("name", 0);
        car.move(new ForwardStrategy());
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("자동차가 움직일 수 없는 이동하지 않는다.")
    @Test
    void stop() {
        Car car = new Car("jason", 0);
        car.move(new HoldStrategy());
        assertThat(car.getPosition()).isZero();
    }


}