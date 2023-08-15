package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class CarTest {

    @DisplayName("자동차는 이름을 가지고 있다")
    @Test
    void name() {
        final var actual = new Car("test");
        assertThat(actual.getName()).isEqualTo("test");
    }

    @DisplayName("자동차는 이름이 5글자를 넘으면 예외가 발생한다.")
    @Test
    void invalid_name() {
        assertThatThrownBy(() -> new Car("testttt"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("자동차는 무작위 값이 4 이상인 경우 전진한다.")
    @Test
    void move() {
        final var car = new Car("test");
        car.move(new NumberMoveCondition(4));
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("자동차는 무작위 값이 3 이하인 경우 정지한다.")
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void stop(final int condition) {
        final var car = new Car("test");
        car.move(new NumberMoveCondition(condition));
        assertThat(car.getPosition()).isZero();
    }

    @DisplayName("자동차는 정지한다.")
    @Test
    void stop() {
        final var car = new Car("test");
        car.move(new StopMoveCondition());
        assertThat(car.getPosition()).isZero();
    }

}
