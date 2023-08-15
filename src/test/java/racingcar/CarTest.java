package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import racingcar.util.NumberMoveStrategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CarTest {

    @DisplayName("자동차 이름")
    @Nested
    class Name {
        @DisplayName("자동차는 이름을 가질 수 있다.")
        @Test
        void name() {
            var car = new Car("name");

            assertThat(car.getName()).isEqualTo("name");
        }

        @DisplayName("자동차 이름은 5글자를 넘을 수 없다.")
        @Test
        void invalid_name() {
            assertThatThrownBy(() -> new Car("123456"))
                .isInstanceOf(IllegalArgumentException.class);
        }
    }


    @DisplayName("자동차 이동")
    @Nested
    class Move {
        @DisplayName("자동차는 무작위 값이 4 이상인 경우 전진한다.")
        @ParameterizedTest
        @ValueSource(ints = {4, 5, 6, 7, 8, 9})
        void move(int num) {
            var car = new Car("name");

            car.move(new NumberMoveStrategy(num));

            assertThat(car.getPosition()).isEqualTo(1);
        }

        @DisplayName("자동차는 무작위 값이 4 미만이면 이동하지 않는다.")
        @ParameterizedTest
        @ValueSource(ints = {1, 2, 3})
        void no_move(int num) {
            var car = new Car("name");

            car.move(new NumberMoveStrategy(num));

            assertThat(car.getPosition()).isEqualTo(0);
        }
    }

}
