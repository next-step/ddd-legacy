package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class CarTest {

    @DisplayName("자동차는 이름을 가진다.")
    @Test
    void name() {
        final var car = new Car("네글자차");
        assertThat(car.getName()).isEqualTo("네글자차");
    }

    @DisplayName("자동차 이름은 5글자를 넘을 수 없다")
    @Test
    void invalid_name() {
        assertThatThrownBy(() -> new Car("여섯글자이름"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("자동차의 이름은 5글자를 넘을 수 없습니다.");
    }

    @DisplayName("자동차는 무작위 값이 4 이상인 경우 전진한다")
    @Test
    void move() {
        final Car car = new Car("네글자차");
        car.move(new NumberMoveCondition(4));
        assertThat(car.getPosition()).isOne();
    }

    @DisplayName("자동차는 무작위 값이 3 이하인 경우 멈춘다.")
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void stop(final int condition) {
        final Car car = new Car("네글자차");
        car.move(new NumberMoveCondition(condition));
        assertThat(car.getPosition()).isZero();
    }

    @DisplayName("자동차는 멈춘다.")
    @Test
    void stop2() {
        final Car car = new Car("네글자차");
        car.move(new StopMoveCondition());
        assertThat(car.getPosition()).isZero();
    }
}
