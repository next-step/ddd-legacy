package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CarTest {
    @DisplayName("자동차는 이름을 가지고 있다")
    @Test
    void name() {
        final var actual = new Car("MIN");
        assertThat(actual.getName()).isEqualTo("MIN");
    }

    @DisplayName("자동차 이름은 5 글자를 넘을 수 없다")
    @Test
    void invalid_name() {
        assertThatThrownBy(() -> new Car("얄리얄리얄라셩"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("자동차는 무작위 값이 4이상인 경우 전진한다")
    @Test
    void move() {
        final Car car = new Car("MIN");
        car.move(new NumberMoveCondition(4));
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("자동차는 무작위 값이 3이하인 경우 정지한다")
    @ValueSource(ints = {0, 1, 2, 3})
    @ParameterizedTest
    void stop(final int condition) {
        final Car car = new Car("MIN");
        car.move(new NumberMoveCondition(condition));
        assertThat(car.getPosition()).isZero();
    }

    @DisplayName("자동차는 정지한다")
    @Test
    void stop() {
        final Car car = new Car("MIN");
        car.move(new StopMoveCondition());
        assertThat(car.getPosition()).isZero();
    }

}
