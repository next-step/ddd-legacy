package racingcar;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;


class CarTest {


    @DisplayName("Car의 name은 5글자를 넘으면 예외가 발생한다")
    @Test
    void constructor() {
        assertThatIllegalArgumentException().isThrownBy(() -> new Car("abcdef"));
    }

    @DisplayName("자동차는 움직인다.")
    @Test
    void move() {
        final var car = new Car("jay");
        car.move(new ForwardStrategy());
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("자동차는 정지한다.")
    @Test
    void stop() {
        final var car = new Car("jay");
        car.move(new StopStrategy());
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("무작위 값이 4 이상이면 자동차는 움직인다")
    @ValueSource(ints = {4, 5, 6, 7, 8, 9})
    @ParameterizedTest
    void move(final int condition) {
        final var car = new Car("jay");
        car.move(condition);
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("무작위 값이 4 이하이면 자동차는 정지한다")
    @ValueSource(ints = {0, 1, 2, 3})
    @ParameterizedTest
    void stop(final int condition) {
        final var car = new Car("jay");
        car.move(condition);
        assertThat(car.getPosition()).isEqualTo(0);
    }


}