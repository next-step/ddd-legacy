package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CarTest {

    @DisplayName("자동차는 이름을 가지고 있다.")
    @Test
    void have_a_name() {
        // given
        String carName = "spcha";

        // when
        final var actual = new Car(carName);

        // then
        assertThat(actual.getName()).isEqualTo(carName);

    }

    @DisplayName("자동차의 이름이 5글자를 넘으면 예외를 발생시킨다.")
    @Test
    void invalid_name() {
        // given
        String carName = "spchap";

        // when & then
        assertThatThrownBy(() -> new Car(carName))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("자동차는 무작위 값이 4 이상인 값인 경우 전진한다.")
    @Test
    void move() {
        // given
        String carName = "spcha";

        // when
        final Car car = new Car(carName);
        car.move(new NumberMoveCondition(4));

        // then
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("자동차는 무작위 값이 3 이하인 경우 정지한다.")
    @ValueSource(ints = {0, 1, 2, 3})
    @ParameterizedTest
    void stop(final int condition) {
        // given
        String carName = "spcha";

        // when
        final Car car = new Car(carName);
        car.move(new NumberMoveCondition(condition));

        // then
        assertThat(car.getPosition()).isZero();
    }

    @DisplayName("자동차는 정지한다.")
    @Test
    void stop2() {
        // given
        String carName = "spcha";

        // when
        final Car car = new Car(carName);
        car.move(new StopMoveCondition());

        // then
        assertThat(car.getPosition()).isZero();
    }

}
