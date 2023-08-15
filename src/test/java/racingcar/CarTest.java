package racingcar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CarTest {

    @DisplayName("자동차는 이름을 가진다")
    @Test
    void name() {
        var actual = new Car("roki");
        assertThat(actual.getName()).isEqualTo("roki");
    }

    @DisplayName("자동차의 이름이 5글자를 넘으면 예외가 발생한다")
    @Test
    void invalidName() {
        // given
        var longName = "veryLongName";

        // when // then
        assertThatThrownBy(() -> new Car(longName))
            .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("자동차는 무작위 값이 4 이상인 경우에 전진한다")
    @ParameterizedTest
    @ValueSource(ints = {4, 5})
    void moveIfConditionIsBiggerAndEqualThanMovingCondition(int condition) {
        // given
        var car = new Car("roki");

        // when
        car.move(new NumberMoveCondition(condition));

        // then
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("자동차는 무작위 값이 4 미만인 경우에 정지한다")
    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    void notMoveIfConditionIsBiggerAndEqualThanMovingCondition(int condition) {
        // given
        var car = new Car("roki");

        // when
        car.move(new NumberMoveCondition(condition));

        // then
        assertThat(car.getPosition()).isZero();
    }

    @DisplayName("자동차는 정지한다")
    @Test
    void stop() {
        // given
        Car car = new Car("jason");

        // when // then
        car.move(new StopMoveCondition());
    }
}
