package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CarTest {

    @Test
    @DisplayName("자동차 이름은 5 글자를 넘을 수 없다.")
    void createCar() {
        // given
        final String name = "abcdef";

        // when & then
        assertThrows(IllegalArgumentException.class, () -> new Car(name));
    }

    @ParameterizedTest
    @ValueSource(ints = {4, 5, 6, 7, 8, 9})
    @DisplayName("숫자가 4 이상이면 자동차가 이동한다.")
    void moveSuccess(final int condition) {
        final Car car = new Car("car");
        car.move(condition);
        assertThat(car.getPosition()).isEqualTo(1);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3})
    @DisplayName("숫자가 4 미만이면 자동차는 움직이지 않는다.")
    void stop(final int condition) {
        final Car car = new Car("car");
        car.move(condition);
        assertThat(car.getPosition()).isEqualTo(0);
    }

}
