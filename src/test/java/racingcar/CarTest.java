package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

public class CarTest {

    @DisplayName("자동차는 이름을 가지고 있다")
    @Test
    void name() {
        final var actual = new Car("Jason");
        assertThat(actual.getName()).isEqualTo("Jason");
    }

    @DisplayName("자동차의 이름이 5글자를 넘으면 예외가 발생한다")
    @Test
    void invalid_name() {
        assertThatThrownBy(() -> new Car("동해물과백두산이"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("자동차는 무작위 값이 4이상인 경우 전진한다")
    @Test
    void move() {
        Car car = new Car("Jason");
        car.move(new NumMoveCondition(4));
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("자동차는 무작위 값이 4 미만인 경우 정지한다")
    @ValueSource(ints = {0,1,2,3})
    @ParameterizedTest
    void stop(final int condition) {
        Car car = new Car("Jason");
        car.move(new NumMoveCondition(condition));
        assertThat(car.getPosition()).isZero();
    }

    @DisplayName("자동차는 정지한다")
    @Test
    void stop() {
        Car car = new Car("Jason");
        car.move(new StopMoveCondition());
        assertThat(car.getPosition()).isZero();
    }

}

