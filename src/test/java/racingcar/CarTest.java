package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class CarTest {
    @Test
    @DisplayName("Car는 이름을 가진다")
    void construct() {
        Car car = new Car("osw");
    }

    @Test
    @DisplayName("이름이 5글자를 넘어가면 Exception 발생한다")
    void nameLengthLimit() {
        assertThatThrownBy(() -> {
            new Car("123456");
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @DisplayName("이름이 빈 문자열 or null 이면 Exception 발생한다")
    @NullAndEmptySource
    void noEmptyName(final String name) {
        assertThatThrownBy(() -> {
            new Car(name);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("movingStrategy 에 따라 움직인다")
    void forwardMovingStrategy() {
        Car car = new Car("osw");

        car.move(new ForwardMovingStrategy());

        assertThat(car.getPosition()).isEqualTo(1);
    }

    @Test
    @DisplayName("movingStrategy 에 따라 움직인다")
    void holdMovingStrategy() {
        Car car = new Car("osw");

        car.move(new HoldMovingStrategy());

        assertThat(car.getPosition()).isEqualTo(0);
    }
}