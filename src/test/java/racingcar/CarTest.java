package racingcar;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.*;

class CarTest {
    @DisplayName("자동차 이름은 5글자 이하이다.")
    @Test
    void constructor() {
        assertThatNoException()
                .isThrownBy(() -> new Car("jason"));
    }

    @DisplayName("자동차 이름은 5글자를 넘을 수 없다.")
    @Test
    void constructor_with_illegal_name() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Car("동해물과 백두산이"));
    }

    @DisplayName("자동차 이름은 비어있을 수 없다.")
    @NullAndEmptySource
    @ParameterizedTest
    void constructor_with_null_and_empty_name(String name) {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> new Car(name));
    }

    @DisplayName("자동차는 움직일 수 있는 경우 이동한다.")
    @Test
    void move() {
        Car car = new Car("jason", 0);

        car.move(new ForwardStrategy());

        assertThat(car.getPosition()).isEqualTo(1);
    }

    @DisplayName("자동차는 움직일 수 없는 경우 정지한다.")
    @Test
    void stop() {
        Car car = new Car("jason", 0);

        car.move(new HoldStrategy());

        assertThat(car.getPosition()).isZero();
    }
}
