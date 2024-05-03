package racingcar.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import racingcar.Car;
import racingcar.CarMovingStrategy;
import racingcar.NumberCarMovingStrategy;

class CarTest {

    @Test
    @DisplayName("자동차 이름은 null 이 될 수 없다")
    void nullNameTest() {
        // given
        String name = null;

        // when then
        assertThrows(IllegalArgumentException.class, () -> new Car(name));
    }

    @Test
    @DisplayName("자동차 이름은 5글자를 넘을 수 없다")
    void maxLengthTest() {
        // given
        String name = "abcdef";

        // when then
        assertThrows(IllegalArgumentException.class, () -> new Car(name));
    }

    @ParameterizedTest
    @ValueSource(ints= {4,5,6,7,8,9})
    @DisplayName("이동하고나면 위치가 1이다")
    void testMovable(int moveParameter) {
        Car car = new Car("hello");
        CarMovingStrategy carMovingStrategy = new NumberCarMovingStrategy(()->moveParameter);
        long position = car.move(carMovingStrategy);
        assertThat(position).isOne();
    }

    @ParameterizedTest
    @ValueSource(ints= {0,1,2,3})
    @DisplayName("이동할 수 없어서 위치는 0이다")
    void testNotMovable(int moveParameter) {
        Car car = new Car("hello");
        CarMovingStrategy carMovingStrategy = new NumberCarMovingStrategy(()->moveParameter);
        long position = car.move(carMovingStrategy);
        assertThat(position).isZero();
    }
}
